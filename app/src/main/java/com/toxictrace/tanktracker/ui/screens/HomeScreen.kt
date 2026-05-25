package com.toxictrace.tanktracker.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toxictrace.tanktracker.model.PlayerProfile
import com.toxictrace.tanktracker.model.WN8Colors
import com.toxictrace.tanktracker.ui.components.TacticalCard
import com.toxictrace.tanktracker.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(player: PlayerProfile) {
    val scrollState = rememberScrollState()
    val wn8Color = getWn8ComposeColor(player.wn8Value)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Identity ──────────────────────────────────────────────────────────
        IdentityCard(player, wn8Color)

        // ── WN8 + Win Rate ────────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.weight(1.2f)) { Wn8Card(player.wn8Value, wn8Color) }
            Box(modifier = Modifier.weight(1f)) { WinRateCard(player.winRatePct) }
        }

        // ── Divider label ─────────────────────────────────────────────────────
        SectionLabel("COMBAT STATISTICS")

        // ── 3-column grid: main stats ─────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                MiniStatCard("BATTLES", player.battlesPlayed.toString(), Icons.Default.SwapHoriz, SteelGray)
            }
            Box(modifier = Modifier.weight(1f)) {
                MiniStatCard("AVG DMG", player.avgDamage.toString(), Icons.Default.LocalFireDepartment, NeonRed)
            }
            Box(modifier = Modifier.weight(1f)) {
                MiniStatCard("K/D", player.kdRatio.toString(), Icons.AutoMirrored.Filled.TrendingUp, NeonOrange)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                MiniStatCard("SURVIVAL", "${player.survivalRate}%", Icons.Default.Shield, NeonGreen)
            }
            Box(modifier = Modifier.weight(1f)) {
                MiniStatCard("ACCURACY", "${player.accuracyPct}%", Icons.Default.GpsFixed, Color(0xFF38BDF8))
            }
            Box(modifier = Modifier.weight(1f)) {
                MiniStatCard("AVG XP", player.avgXP.toString(), Icons.Default.Star, NeonOrange)
            }
        }

        // ── Extended stats ────────────────────────────────────────────────────
        SectionLabel("EXTENDED METRICS")

        TacticalCard {
            ExtendedStatRow("Avg Assist Damage", player.avgAssist.toString(), Icons.Default.Radar, Color(0xFFB4F04A))
            HudDivider()
            ExtendedStatRow("Avg Spotted", player.avgSpotted.toString(), Icons.Default.Visibility, Color(0xFF38BDF8))
            HudDivider()
            ExtendedStatRow("Avg Blocked", player.avgBlocked.toString(), Icons.Default.Security, Color(0xFF94A3B8))
            HudDivider()
            ExtendedStatRow("Avg Frags", player.avgFrags.toString(), Icons.Default.FlashOn, NeonRed)
            HudDivider()
            ExtendedStatRow("Avg Tier", player.avgTier.toString(), Icons.Default.Layers, NeonOrange)
        }

        // ── Records ────────────────────────────────────────────────────────────
        SectionLabel("PERSONAL RECORDS")

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                RecordCard("MAX DAMAGE", player.maxDamage.toString(), NeonRed)
            }
            Box(modifier = Modifier.weight(1f)) {
                RecordCard("MAX FRAGS", player.maxFrags.toString(), NeonOrange)
            }
        }

        // ── Garage summary ────────────────────────────────────────────────────
        SectionLabel("GARAGE OVERVIEW")

        TacticalCard {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                GarageItem("VEHICLES", player.tanks.size.toString())
                GarageItem("TOP TIER", "X: ${player.tanks.count { it.tier == 10 }}")
                GarageItem("PREMIUM", player.tanks.count { it.isPremium }.toString())
            }
        }

        // ── Last battle time ──────────────────────────────────────────────────
        if (player.lastBattleTime > 0) {
            val sdf = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
            val lastBattle = remember(player.lastBattleTime) {
                sdf.format(Date(player.lastBattleTime * 1000))
            }
            TacticalCard {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AccessTime, null, tint = SteelGray, modifier = Modifier.size(12.dp))
                    Text("Last battle: $lastBattle", color = SteelGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ── Components ────────────────────────────────────────────────────────────────

@Composable
private fun IdentityCard(player: PlayerProfile, wn8Color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = listOf(DarkSurfaceLighter, DarkSurface),
                    start = Offset(0f, 0f), end = Offset(Float.POSITIVE_INFINITY, 0f)
                ),
                RoundedCornerShape(12.dp)
            )
            .border(1.dp, wn8Color.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(player.nickname, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                    if (player.clanTag.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .background(NeonOrange.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .border(1.dp, NeonOrange.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) { Text("[${player.clanTag}]", color = NeonOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    }
                }
                if (player.clanName.isNotEmpty()) {
                    Text(player.clanName, color = SteelGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("GLOBAL PR", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                Text("#${player.globalRating}", color = NeonOrange, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun Wn8Card(wn8: Int, wn8Color: Color) {
    val label = WN8Colors.getLabel(wn8)
    TacticalCard(borderColor = wn8Color.copy(alpha = 0.35f)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { (wn8.toFloat() / 3500f).coerceIn(0f, 1f) },
                    modifier = Modifier.size(72.dp),
                    color = wn8Color, strokeWidth = 5.dp, trackColor = Color.Black
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("WN8", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Text(wn8.toString(), color = wn8Color, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            }
            Column {
                Box(
                    modifier = Modifier
                        .background(wn8Color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .border(1.dp, wn8Color.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) { Text(label.uppercase(), color = wn8Color, fontSize = 9.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace) }
            }
        }
    }
}

@Composable
private fun WinRateCard(winRate: Double) {
    val color = when {
        winRate >= 60 -> NeonGreen
        winRate >= 54 -> Color(0xFF84DFAF)
        winRate >= 50 -> NeonOrange
        else -> NeonRed
    }
    TacticalCard(borderColor = color.copy(alpha = 0.3f)) {
        Text("WIN RATE", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(6.dp))
        Text("$winRate%", color = color, fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { (winRate / 100).toFloat() },
            modifier = Modifier.fillMaxWidth().height(3.dp),
            color = color, trackColor = Color.Black
        )
    }
}

@Composable
private fun MiniStatCard(label: String, value: String, icon: ImageVector, iconColor: Color) {
    TacticalCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = SteelGray, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
            Icon(icon, null, tint = iconColor.copy(alpha = 0.7f), modifier = Modifier.size(12.dp))
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(value, color = GridOffWhite, fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun ExtendedStatRow(label: String, value: String, icon: ImageVector, iconColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(13.dp))
            Text(label, color = SteelGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
        Text(value, color = GridOffWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun RecordCard(label: String, value: String, color: Color) {
    TacticalCard(borderColor = color.copy(alpha = 0.25f)) {
        Text(label, color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun GarageItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(3.dp))
        Text(value, color = GridOffWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.width(2.dp).height(10.dp).background(NeonOrange, RoundedCornerShape(1.dp)))
        Text(text, color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
private fun HudDivider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkCardBorder))
}
