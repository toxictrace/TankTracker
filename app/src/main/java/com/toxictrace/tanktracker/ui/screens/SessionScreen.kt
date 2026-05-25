package com.toxictrace.tanktracker.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toxictrace.tanktracker.model.PlayerProfile
import com.toxictrace.tanktracker.model.SessionBattle
import com.toxictrace.tanktracker.model.SessionStats
import com.toxictrace.tanktracker.ui.components.TacticalCard
import com.toxictrace.tanktracker.ui.theme.*
import com.toxictrace.tanktracker.viewmodel.SessionViewModel

@Composable
fun SessionScreen(profile: PlayerProfile) {
    val vm: SessionViewModel = viewModel()
    val session by vm.session.collectAsState()
    val timerSec by vm.timerSeconds.collectAsState()
    val isRunning by vm.isRunning.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBg).verticalScroll(scrollState).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("SESSION TRACKER", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                Text("Live Battle Monitor", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }
            // Timer
            Box(
                modifier = Modifier
                    .background(if (isRunning) NeonGreen.copy(0.1f) else DarkSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, if (isRunning) NeonGreen.copy(0.4f) else DarkCardBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isRunning) PulsingDot()
                    Text(
                        vm.formatDuration(timerSec),
                        color = if (isRunning) NeonGreen else SteelGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // ── Start / Stop controls ─────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (!isRunning) {
                Button(
                    onClick = {
                        vm.startSession(profile)
                        vm.startPolling(profile.accountId)
                    },
                    modifier = Modifier.weight(1f).height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("START SESSION", color = Color.Black, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                }
            } else {
                Button(
                    onClick = { vm.stopSession() },
                    modifier = Modifier.weight(1f).height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Stop, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("STOP", color = Color.White, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                }
            }
            OutlinedButton(
                onClick = { vm.resetSession() },
                modifier = Modifier.height(44.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Refresh, null, tint = SteelGray, modifier = Modifier.size(16.dp))
            }
        }

        if (session.battleCount == 0 && !isRunning) {
            // Empty state
            TacticalCard {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Timer, null, tint = SteelGray, modifier = Modifier.size(36.dp))
                    Text("NO ACTIVE SESSION", color = SteelGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text("Press START to begin tracking", color = SteelGray.copy(0.6f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Stats update every 30 seconds", color = SteelGray.copy(0.4f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                }
            }
        } else {
            // ── Session stats grid ────────────────────────────────────────────
            SessionStatsGrid(session)

            // ── Streak indicator ──────────────────────────────────────────────
            StreakCard(session)

            // ── Win rate progress ─────────────────────────────────────────────
            TacticalCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("SESSION WIN RATE", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    val wrColor = if (session.winRate >= 50) NeonGreen else NeonRed
                    Text("${"%.1f".format(session.winRate)}%", color = wrColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (session.winRate / 100).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(5.dp),
                    color = if (session.winRate >= 50) NeonGreen else NeonRed,
                    trackColor = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("${session.wins}W · ${session.battleCount - session.wins}L · ${session.battleCount} total",
                    color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            }

            // ── Best / Worst battle ───────────────────────────────────────────
            if (session.battleHistory.isNotEmpty()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    session.bestBattle?.let { b ->
                        Box(modifier = Modifier.weight(1f)) {
                            HighlightBattleCard("BEST BATTLE", b, NeonGreen)
                        }
                    }
                    session.worstBattle?.let { b ->
                        Box(modifier = Modifier.weight(1f)) {
                            HighlightBattleCard("WORST BATTLE", b, NeonRed)
                        }
                    }
                }

                // ── Battle timeline ───────────────────────────────────────────
                TacticalCard {
                    Text("BATTLE TIMELINE", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    session.battleHistory.reversed().take(10).forEach { b ->
                        BattleTimelineRow(b)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SessionStatsGrid(session: SessionStats) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.weight(1f)) { SessionStatCard("BATTLES", session.battleCount.toString(), SteelGray) }
        Box(modifier = Modifier.weight(1f)) { SessionStatCard("WIN RATE", "${"%.1f".format(session.winRate)}%", if (session.winRate >= 50) NeonGreen else NeonRed) }
        Box(modifier = Modifier.weight(1f)) { SessionStatCard("AVG DMG", session.avgDamage.toString(), NeonOrange) }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.weight(1f)) { SessionStatCard("FRAGS", session.totalFrags.toString(), NeonRed) }
        Box(modifier = Modifier.weight(1f)) { SessionStatCard("SURVIVAL", "${"%.0f".format(session.survivalRate)}%", NeonGreen) }
        Box(modifier = Modifier.weight(1f)) { SessionStatCard("AVG XP", session.avgXP.toString(), Color(0xFFB4F04A)) }
    }
}

@Composable
private fun SessionStatCard(label: String, value: String, color: Color) {
    TacticalCard(borderColor = color.copy(0.2f)) {
        Text(label, color = SteelGray, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = color, fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun StreakCard(session: SessionStats) {
    val ws = session.winStreak
    val ls = session.loseStreak
    if (ws == 0 && ls == 0) return

    val (label, count, color) = if (ws > 0)
        Triple("WIN STREAK", ws, NeonGreen)
    else
        Triple("LOSE STREAK", ls, NeonRed)

    TacticalCard(borderColor = color.copy(0.3f)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(if (ws > 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    null, tint = color, modifier = Modifier.size(16.dp))
                Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
            Text("$count battles", color = color, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun HighlightBattleCard(label: String, battle: SessionBattle, color: Color) {
    TacticalCard(borderColor = color.copy(0.25f)) {
        Text(label, color = color, fontSize = 7.5.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("${battle.damage} DMG", color = GridOffWhite, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
        Text("${battle.frags} frags · ${if (battle.isWin) "WIN" else "LOSS"}",
            color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        if (battle.tankName != "Unknown")
            Text(battle.tankName, color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun BattleTimelineRow(battle: SessionBattle) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(
                if (battle.isWin) NeonGreen.copy(0.05f) else NeonRed.copy(0.05f),
                RoundedCornerShape(4.dp)
            )
            .border(1.dp, if (battle.isWin) NeonGreen.copy(0.15f) else NeonRed.copy(0.15f), RoundedCornerShape(4.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.size(8.dp, 8.dp)
                    .background(if (battle.isWin) NeonGreen else NeonRed, RoundedCornerShape(2.dp))
            )
            Text(if (battle.tankName != "Unknown") battle.tankName else "Battle",
                color = GridOffWhite, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("${battle.damage} DMG", color = NeonOrange, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            Text("${battle.frags}K", color = NeonRed, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Text("${battle.xp}XP", color = Color(0xFFB4F04A), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun PulsingDot() {
    val inf = rememberInfiniteTransition(label = "pulse")
    val alpha by inf.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "alpha"
    )
    Box(
        modifier = Modifier.size(7.dp)
            .alpha(alpha)
            .background(NeonGreen, RoundedCornerShape(50))
    )
}
