package com.toxictrace.tanktracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toxictrace.tanktracker.api.VehicleData
import com.toxictrace.tanktracker.api.WgApiClient
import com.toxictrace.tanktracker.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

sealed class CompareState {
    object Idle : CompareState()
    object Loading : CompareState()
    data class Ready(val profile: PlayerProfile) : CompareState()
    data class Error(val message: String) : CompareState()
}

class CompareViewModel : ViewModel() {

    private val _state = MutableStateFlow<CompareState>(CompareState.Idle)
    val state: StateFlow<CompareState> = _state

    private val api = WgApiClient.api
    private val appId = WgApiClient.APP_ID

    fun loadOpponent(nickname: String) {
        viewModelScope.launch {
            _state.value = CompareState.Loading
            try {
                // 1. Search
                val searchResp = api.searchPlayers(appId, nickname, type = "exact")
                val found = searchResp.data?.firstOrNull()
                    ?: run { _state.value = CompareState.Error("Player \"$nickname\" not found"); return@launch }

                val accountId = found.accountId

                // 2. Account info
                val accountResp = api.getAccountInfo(appId, accountId)
                val accountData = accountResp.data?.get(accountId.toString())
                    ?: run { _state.value = CompareState.Error("Could not load account data"); return@launch }

                val s = accountData.statistics?.all
                val battles  = s?.battles ?: 0
                val wins     = s?.wins ?: 0
                val frags    = s?.frags ?: 0
                val survived = s?.survivedBattles ?: 0
                val hits     = s?.hits ?: 0
                val shots    = s?.shots ?: 0
                val dmg      = s?.damageDealt ?: 0L
                val assistR  = s?.damageAssistedRadio ?: 0L
                val assistT  = s?.damageAssistedTrack ?: 0L
                val blocked  = s?.damageBlockedByArmour ?: 0L
                val spotted  = s?.spotted ?: 0
                val xp       = s?.xp ?: 0L

                val b = battles.coerceAtLeast(1)
                val deaths = (battles - survived).coerceAtLeast(1)

                val winRate    = (wins.toDouble() / b * 100).roundTo(2)
                val avgDmg     = (dmg / b).toInt()
                val avgAssist  = ((assistR + assistT) / b).toInt()
                val avgBlocked = (blocked / b).toInt()
                val avgSpotted = (spotted.toDouble() / b).roundTo(2)
                val avgFrags   = (frags.toDouble() / b).roundTo(2)
                val avgXP      = (xp / b).toInt()
                val kdRatio    = (frags.toDouble() / deaths).roundTo(2)
                val survival   = (survived.toDouble() / b * 100).roundTo(1)
                val accuracy   = if (shots > 0) (hits.toDouble() / shots * 100).roundTo(1) else 0.0

                // 3. Clan
                var clanTag = ""; var clanName = ""
                try {
                    val cr = api.getPlayerClan(appId, accountId)
                    cr.data?.get(accountId.toString())?.clan?.let {
                        clanTag = it.tag; clanName = it.name
                    }
                } catch (_: Exception) {}

                // 4. Tanks (top 100 by battles for speed)
                val tanks = mutableListOf<TankInfo>()
                try {
                    val tankResp = api.getTankStats(appId, accountId)
                    val statList = tankResp.data?.get(accountId.toString())
                        ?.filter { (it.all?.battles ?: 0) > 0 }
                        ?.sortedByDescending { it.all?.battles ?: 0 }
                        ?.take(100)
                        ?: emptyList()

                    val vehicleMap = mutableMapOf<Long, VehicleData>()
                    statList.chunked(100).forEach { batch ->
                        try {
                            val ids = batch.joinToString(",") { it.tankId.toString() }
                            api.getVehicleInfo(appId, ids).data?.forEach { (k, v) ->
                                if (v != null) vehicleMap[k.toLong()] = v
                            }
                        } catch (_: Exception) {}
                    }

                    statList.forEach { stat ->
                        val tb = stat.all?.battles ?: 0
                        if (tb == 0) return@forEach
                        val tw  = stat.all?.wins ?: 0
                        val td  = stat.all?.damageDealt ?: 0L
                        val tar = stat.all?.damageAssistedRadio ?: 0L
                        val tat = stat.all?.damageAssistedTrack ?: 0L
                        val tbl = stat.all?.damageBlockedByArmour ?: 0L
                        val tf  = stat.all?.frags ?: 0
                        val tsv = stat.all?.survivedBattles ?: 0
                        val th  = stat.all?.hits ?: 0
                        val ts  = stat.all?.shots ?: 0
                        val tsp = stat.all?.spotted ?: 0
                        val tx  = stat.all?.xp ?: 0L
                        val veh = vehicleMap[stat.tankId]
                        tanks.add(TankInfo(
                            id = stat.tankId,
                            name = veh?.name ?: "Tank #${stat.tankId}",
                            nation = veh?.nation?.replaceFirstChar { it.uppercase() } ?: "Unknown",
                            tier = veh?.tier ?: 0,
                            tankClass = veh?.type?.toTankClass() ?: TankClass.MEDIUM,
                            isPremium = veh?.isPremium ?: false,
                            battles = tb,
                            winRate = (tw.toDouble() / tb * 100).roundTo(2),
                            avgDamage = (td / tb).toInt(),
                            avgAssist = ((tar + tat) / tb).toInt(),
                            avgBlocked = (tbl / tb).toInt(),
                            avgSpotted = (tsp.toDouble() / tb).roundTo(2),
                            avgFrags = (tf.toDouble() / tb).roundTo(2),
                            avgXP = (tx / tb).toInt(),
                            survivalRate = (tsv.toDouble() / tb * 100).roundTo(1),
                            accuracy = if (ts > 0) (th.toDouble() / ts * 100).roundTo(1) else 0.0,
                            marksOfExcellence = 0,
                            masteryBadge = stat.markOfMastery.toMasteryBadge(),
                            imageUrl = veh?.images?.bigIcon
                        ))
                    }
                } catch (_: Exception) {}

                _state.value = CompareState.Ready(PlayerProfile(
                    accountId = accountId,
                    nickname = found.nickname,
                    clanTag = clanTag, clanName = clanName,
                    globalRating = accountData.globalRating,
                    createdAt = accountData.createdAt,
                    lastBattleTime = accountData.lastBattleTime,
                    wn8Value = approxWn8(winRate, avgDmg, battles),
                    winRatePct = winRate, battlesPlayed = battles,
                    avgDamage = avgDmg, avgAssist = avgAssist,
                    avgBlocked = avgBlocked, avgSpotted = avgSpotted,
                    avgFrags = avgFrags, kdRatio = kdRatio,
                    survivalRate = survival, accuracyPct = accuracy,
                    avgXP = avgXP, avgTier = 0.0,
                    maxDamage = s?.maxDamage ?: 0,
                    maxFrags = s?.maxFrags ?: 0,
                    tanks = tanks
                ))

            } catch (e: Exception) {
                _state.value = CompareState.Error("Error: ${e.message}")
            }
        }
    }

    private fun approxWn8(winRate: Double, avgDmg: Int, battles: Int): Int {
        if (battles < 10) return 0
        return ((avgDmg / 10.0) + (winRate - 49.0) * 40).toInt().coerceIn(0, 4000)
    }

    private fun Double.roundTo(d: Int): Double {
        var m = 1.0; repeat(d) { m *= 10 }
        return (this * m).roundToInt() / m
    }
}
