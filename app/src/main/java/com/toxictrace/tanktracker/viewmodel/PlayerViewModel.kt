package com.toxictrace.tanktracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toxictrace.tanktracker.api.PlayerSearchItem
import com.toxictrace.tanktracker.api.WgApiClient
import com.toxictrace.tanktracker.model.MasteryBadge
import com.toxictrace.tanktracker.model.PlayerProfile
import com.toxictrace.tanktracker.model.TankInfo
import com.toxictrace.tanktracker.model.toMasteryBadge
import com.toxictrace.tanktracker.model.toTankClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val profile: PlayerProfile) : UiState()
    data class SearchResults(val results: List<PlayerSearchItem>) : UiState()
    data class Error(val message: String) : UiState()
}

class PlayerViewModel : ViewModel() {

    private val api = WgApiClient.api
    private val appId = WgApiClient.APP_ID

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // ── Search players ────────────────────────────────────────────────────────

    fun searchPlayers(query: String) {
        if (query.length < 3) return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val resp = api.searchPlayers(appId, query)
                val items = resp.data ?: emptyList()
                if (items.isEmpty()) {
                    _uiState.value = UiState.Error("No players found for \"$query\"")
                } else {
                    _uiState.value = UiState.SearchResults(items)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Network error: ${e.message}")
            }
        }
    }

    // ── Load full profile by accountId ────────────────────────────────────────

    fun loadProfile(accountId: Long) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // 1. Account info
                val accountResp = api.getAccountInfo(appId, accountId)
                val accountData = accountResp.data?.get(accountId.toString())
                    ?: run { _uiState.value = UiState.Error("Account not found"); return@launch }

                // 2. Clan info
                var clanTag = ""
                var clanName = ""
                try {
                    val clanResp = api.getPlayerClan(appId, accountId)
                    val clanData = clanResp.data?.get(accountId.toString())
                    clanTag = clanData?.clan?.tag ?: ""
                    clanName = clanData?.clan?.name ?: ""
                } catch (_: Exception) {}

                // 3. Tank stats
                val tankStatsResp = api.getTankStats(appId, accountId)
                val tankStatsList = tankStatsResp.data?.get(accountId.toString()) ?: emptyList()

                // 4. Vehicle encyclopedia (batch, max 100 tanks)
                val topTanks = tankStatsList
                    .filter { (it.all?.battles ?: 0) > 0 }
                    .sortedByDescending { it.all?.battles ?: 0 }
                    .take(100)

                val vehicleMap = mutableMapOf<Long, com.toxictrace.tanktracker.api.VehicleData>()
                if (topTanks.isNotEmpty()) {
                    try {
                        val ids = topTanks.joinToString(",") { it.tankId.toString() }
                        val vehicleResp = api.getVehicleInfo(appId, ids)
                        vehicleResp.data?.forEach { (k, v) ->
                            if (v != null) vehicleMap[k.toLong()] = v
                        }
                    } catch (_: Exception) {}
                }

                // 5. Build TankInfo list
                val tanks = topTanks.mapNotNull { stat ->
                    val veh = vehicleMap[stat.tankId] ?: return@mapNotNull null
                    val battles = stat.all?.battles ?: 0
                    if (battles == 0) return@mapNotNull null
                    val wins = stat.all?.wins ?: 0
                    val dmg = stat.all?.damageDealt ?: 0L
                    val xp = stat.all?.xp ?: 0L
                    TankInfo(
                        id = stat.tankId,
                        name = veh.name,
                        nation = veh.nation.replaceFirstChar { it.uppercase() },
                        tier = veh.tier,
                        tankClass = veh.type.toTankClass(),
                        battles = battles,
                        winRate = (wins.toDouble() / battles * 100).roundTo(2),
                        avgDamage = if (battles > 0) (dmg / battles).toInt() else 0,
                        avgXP = if (battles > 0) (xp / battles).toInt() else 0,
                        marksOfExcellence = stat.marksOnGun ?: 0,
                        masteryBadge = stat.markOfMastery.toMasteryBadge(),
                        imageUrl = veh.images?.bigIcon
                    )
                }

                // 6. Global stats from account
                val stats = accountData.statistics?.all
                val battles = stats?.battles ?: 0
                val wins = stats?.wins ?: 0
                val frags = stats?.frags ?: 0
                val survived = stats?.survived_battles ?: 0
                val hits = stats?.hits ?: 0
                val shots = stats?.shots ?: 0
                val totalDmg = tanks.sumOf { it.avgDamage.toLong() * it.battles }
                val totalBattles = tanks.sumOf { it.battles }.coerceAtLeast(1)
                val avgDmg = (totalDmg / totalBattles).toInt()
                val losses = stats?.losses ?: 0
                val kdRatio = if (losses > 0) (frags.toDouble() / (battles - survived).coerceAtLeast(1)).roundTo(2) else frags.toDouble()
                val survivalRate = if (battles > 0) (survived.toDouble() / battles * 100).roundTo(1) else 0.0
                val accuracy = if (shots > 0) (hits.toDouble() / shots * 100).roundTo(1) else 0.0
                val winRatePct = if (battles > 0) (wins.toDouble() / battles * 100).roundTo(2) else 0.0
                val avgXP = if (battles > 0) ((stats?.xp ?: 0L) / battles).toInt() else 0

                // Simple WN8 approximation from win rate + avg damage
                val approxWn8 = approximateWn8(winRatePct, avgDmg, battles)

                val profile = PlayerProfile(
                    accountId = accountId,
                    nickname = accountData.nickname,
                    clanTag = clanTag,
                    clanName = clanName,
                    globalRating = accountData.globalRating,
                    wn8Value = approxWn8,
                    winRatePct = winRatePct,
                    battlesPlayed = battles,
                    avgDamage = avgDmg,
                    kdRatio = kdRatio,
                    survivalRate = survivalRate,
                    accuracyPct = accuracy,
                    avgXP = avgXP,
                    tanks = tanks
                )

                _uiState.value = UiState.Success(profile)

            } catch (e: Exception) {
                _uiState.value = UiState.Error("Failed to load profile: ${e.message}")
            }
        }
    }

    fun resetToIdle() {
        _uiState.value = UiState.Idle
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    // Very rough WN8 approximation (real WN8 needs expected values table)
    private fun approximateWn8(winRate: Double, avgDmg: Int, battles: Int): Int {
        if (battles < 10) return 0
        val winFactor = (winRate - 49.0) * 40
        val dmgFactor = (avgDmg / 10.0)
        return (dmgFactor + winFactor).toInt().coerceIn(0, 4000)
    }

    private fun Double.roundTo(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (this * multiplier).roundToInt() / multiplier
    }
}
