package com.toxictrace.tanktracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toxictrace.tanktracker.api.WgApiClient
import com.toxictrace.tanktracker.model.PlayerProfile
import com.toxictrace.tanktracker.model.SessionBattle
import com.toxictrace.tanktracker.model.SessionStats
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SessionViewModel : ViewModel() {

    private val _session = MutableStateFlow(SessionStats())
    val session: StateFlow<SessionStats> = _session

    private val _timerSeconds = MutableStateFlow(0L)
    val timerSeconds: StateFlow<Long> = _timerSeconds

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    // Snapshot of stats before session started — used to diff
    private var preSessionProfile: PlayerProfile? = null
    private var timerJob: Job? = null
    private var pollJob: Job? = null

    fun startSession(profile: PlayerProfile) {
        preSessionProfile = profile
        _session.value = SessionStats(startTime = System.currentTimeMillis())
        _timerSeconds.value = 0L
        _isRunning.value = true
        startTimer()
    }

    fun stopSession() {
        _isRunning.value = false
        timerJob?.cancel()
        pollJob?.cancel()
    }

    fun resetSession() {
        stopSession()
        _session.value = SessionStats()
        _timerSeconds.value = 0L
    }

    // Manual battle entry (for demo / future API polling)
    fun addBattle(battle: SessionBattle) {
        val s = _session.value
        _session.value = s.copy(
            battleCount = s.battleCount + 1,
            wins = s.wins + (if (battle.isWin) 1 else 0),
            totalDamage = s.totalDamage + battle.damage,
            totalFrags = s.totalFrags + battle.frags,
            survivedCount = s.survivedCount + (if (battle.survived) 1 else 0),
            totalXP = s.totalXP + battle.xp,
            battleHistory = s.battleHistory + battle
        )
    }

    // Poll WG API for new battles (compares total battles count)
    fun startPolling(accountId: Long) {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            val api = WgApiClient.api
            val appId = WgApiClient.APP_ID
            var lastBattleCount = preSessionProfile?.battlesPlayed ?: 0
            while (isActive && _isRunning.value) {
                delay(30_000) // poll every 30s
                try {
                    val resp = api.getAccountInfo(appId, accountId)
                    val data = resp.data?.get(accountId.toString()) ?: continue
                    val newCount = data.statistics?.all?.battles ?: 0
                    if (newCount > lastBattleCount) {
                        val diff = newCount - lastBattleCount
                        // We know new battles happened but can't get individual battle details
                        // from public API — add aggregate entry
                        val newDmg = data.statistics?.all?.damageDealt ?: 0L
                        val oldDmg = (preSessionProfile?.battlesPlayed?.toLong() ?: 0L) *
                                (preSessionProfile?.avgDamage?.toLong() ?: 0L)
                        val sessionDmg = ((newDmg - oldDmg) / diff.coerceAtLeast(1)).toInt()
                            .coerceAtLeast(0)
                        val newWins = data.statistics?.all?.wins ?: 0
                        val oldWins = preSessionProfile?.let {
                            (it.winRatePct / 100 * it.battlesPlayed).toInt()
                        } ?: 0
                        val wonBattle = newWins > oldWins
                        repeat(diff) {
                            addBattle(
                                SessionBattle(
                                    tankName = "Unknown",
                                    tankTier = 0,
                                    isWin = wonBattle,
                                    damage = sessionDmg,
                                    frags = 0,
                                    survived = false,
                                    xp = data.statistics?.all?.battleAvgXp ?: 0
                                )
                            )
                        }
                        lastBattleCount = newCount
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _isRunning.value) {
                delay(1000)
                _timerSeconds.value++
            }
        }
    }

    fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return if (h > 0) "%02d:%02d:%02d".format(h, m, s)
        else "%02d:%02d".format(m, s)
    }
}
