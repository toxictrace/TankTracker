package com.toxictrace.tanktracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toxictrace.tanktracker.model.PlayerProfile
import com.toxictrace.tanktracker.model.TankClass
import com.toxictrace.tanktracker.ui.components.TacticalCard
import com.toxictrace.tanktracker.ui.theme.*

@Composable
fun TacticsScreen(player: PlayerProfile) {
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBg).verticalScroll(scroll).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text("TACTICAL RADAR", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Text("Performance Analytics", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
        }

        // ── Performance bars ──────────────────────────────────────────────────
        TacticalCard {
            Text("COMBAT EFFICIENCY", color = GridOffWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(12.dp))
            PerformanceBar("Win Rate",      "${player.winRatePct}%", player.winRatePct / 100,  NeonGreen)
            Spacer(modifier = Modifier.height(8.dp))
            PerformanceBar("Shell Accuracy","${player.accuracyPct}%", player.accuracyPct / 100, Color(0xFF38BDF8))
            Spacer(modifier = Modifier.height(8.dp))
            PerformanceBar("Survival Rate", "${player.survivalRate}%", player.survivalRate / 100, NeonOrange)
            Spacer(modifier = Modifier.height(8.dp))
            val kdNorm = (player.kdRatio / 3.0).coerceIn(0.0, 1.0)
            PerformanceBar("K/D Ratio",     player.kdRatio.toString(), kdNorm, NeonRed)
        }

        // ── Damage breakdown ──────────────────────────────────────────────────
        TacticalCard {
            Text("DAMAGE BREAKDOWN", color = GridOffWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(10.dp))
            val total = (player.avgDamage + player.avgAssist + player.avgBlocked).toFloat().coerceAtLeast(1f)
            DamageRow("Direct Damage", player.avgDamage, player.avgDamage / total, NeonRed)
            Spacer(modifier = Modifier.height(6.dp))
            DamageRow("Assist Damage", player.avgAssist, player.avgAssist / total, Color(0xFFB4F04A))
            Spacer(modifier = Modifier.height(6.dp))
            DamageRow("Blocked Damage", player.avgBlocked, player.avgBlocked / total, Color(0xFF94A3B8))
        }

        // ── Class breakdown ───────────────────────────────────────────────────
        if (player.tanks.isNotEmpty()) {
            TacticalCard {
                Text("TANK CLASS DISTRIBUTION", color = GridOffWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(10.dp))
                val totalBattles = player.tanks.sumOf { it.battles }.toFloat().coerceAtLeast(1f)
                TankClass.values().forEach { tc ->
                    val classtanks = player.tanks.filter { it.tankClass == tc }
                    if (classtanks.isEmpty()) return@forEach
                    val classBattles = classtanks.sumOf { it.battles }
                    val classWr = classtanks.sumOf { it.winRate * it.battles } / classBattles
                    val (label, color) = when (tc) {
                        TankClass.HEAVY -> "Heavy Tanks" to NeonRed
                        TankClass.MEDIUM -> "Medium Tanks" to NeonOrange
                        TankClass.LIGHT -> "Light Tanks" to NeonGreen
                        TankClass.TANK_DESTROYER -> "Tank Destroyers" to Color(0xFF38BDF8)
                        TankClass.SPG -> "Artillery" to Color(0xFFD97706)
                    }
                    ClassRow(label, classBattles, "%.1f%%".format(classWr), classBattles / totalBattles, color)
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // ── Top 5 tanks ───────────────────────────────────────────────────
            TacticalCard {
                Text("TOP PERFORMERS (MIN 20 BATTLES)", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                player.tanks.filter { it.battles >= 20 }.sortedByDescending { it.winRate }.take(5).forEach { tank ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(tank.name, color = GridOffWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                            Text("${tank.battles} btl · ${tank.avgDamage} dmg", color = SteelGray, fontSize = 8.sp)
                        }
                        Text("${tank.winRate}%", color = if (tank.winRate >= 50) NeonGreen else NeonRed,
                            fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // ── Multipliers ───────────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.weight(1f)) { MultiplierCard("AVG SPOTTED", player.avgSpotted.toString(), player.avgSpotted >= 1.0, Color(0xFF38BDF8)) }
            Box(modifier = Modifier.weight(1f)) { MultiplierCard("AVG FRAGS", player.avgFrags.toString(), player.avgFrags >= 1.0, NeonRed) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.weight(1f)) { MultiplierCard("AVG TIER", player.avgTier.toString(), player.avgTier >= 7.0, NeonOrange) }
            Box(modifier = Modifier.weight(1f)) { MultiplierCard("AVG XP", player.avgXP.toString(), player.avgXP >= 500, Color(0xFFB4F04A)) }
        }
    }
}

@Composable
private fun PerformanceBar(label: String, value: String, progress: Double, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Text(value, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { progress.toFloat().coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = color, trackColor = Color.Black
        )
    }
}

@Composable
private fun DamageRow(label: String, value: Int, fraction: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Text(value.toString(), color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(progress = { fraction.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(3.dp), color = color, trackColor = Color.Black)
    }
}

@Composable
private fun ClassRow(label: String, battles: Int, wr: String, fraction: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("$battles btl", color = Color(0xFF718294), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                Text(wr, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(progress = { fraction.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(3.dp), color = color, trackColor = Color.Black)
    }
}

@Composable
private fun MultiplierCard(label: String, value: String, isPositive: Boolean, color: Color) {
    val c = if (isPositive) color else NeonRed
    TacticalCard(borderColor = c.copy(0.2f)) {
        Text(label, color = SteelGray, fontSize = 7.5.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = c, fontSize = 17.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
    }
}
