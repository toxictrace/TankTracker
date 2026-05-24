package com.toxictrace.tanktracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toxictrace.tanktracker.model.PlayerProfile
import com.toxictrace.tanktracker.ui.components.TacticalCard
import com.toxictrace.tanktracker.ui.theme.*

@Composable
fun TacticsScreen(player: PlayerProfile) {
    val scrollState = rememberScrollState()

    // Derived metrics from real data
    val fragRatio = (player.kdRatio / 3.0 * 100).toInt().coerceIn(0, 100)
    val survPct = player.survivalRate.toInt().coerceIn(0, 100)
    val accPct = player.accuracyPct.toInt().coerceIn(0, 100)

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBg).verticalScroll(scrollState).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text("SESSION EFFICIENCY", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Text("Tactical Radar", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
        }

        TacticalCard {
            Text("COMBAT SKILL DYNAMICS", color = GridOffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(14.dp))
            RadarRow("Frag Efficiency (K/D)", fragRatio, Icons.Default.PrecisionManufacturing, player.kdRatio.toString())
            Spacer(modifier = Modifier.height(10.dp))
            RadarRow("Survivability Rate", survPct, Icons.Default.Shield, "${player.survivalRate}%")
            Spacer(modifier = Modifier.height(10.dp))
            RadarRow("Shell Accuracy", accPct, Icons.Default.MonitorWeight, "${player.accuracyPct}%")
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier.fillMaxWidth().background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(6.dp)).padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Info, null, tint = NeonOrange, modifier = Modifier.size(12.dp))
                    Text(
                        "Stats from EU server. WN8 is approximated from win rate and avg damage.",
                        color = SteelGray,
                        fontSize = 8.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Text("COMBAT MULTIPLIERS", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                MultiplierCard(
                    label = "WIN RATE",
                    value = "${player.winRatePct}%",
                    status = if (player.winRatePct >= 50) "Positive" else "Negative",
                    isPositive = player.winRatePct >= 50.0
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                MultiplierCard(
                    label = "AVG DAMAGE",
                    value = player.avgDamage.toString(),
                    status = if (player.avgDamage >= 1000) "High" else "Average",
                    isPositive = player.avgDamage >= 1000
                )
            }
        }

        // Top tanks by win rate
        if (player.tanks.isNotEmpty()) {
            TacticalCard {
                Text("TOP TANKS BY WIN RATE", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                player.tanks
                    .filter { it.battles >= 20 }
                    .sortedByDescending { it.winRate }
                    .take(5)
                    .forEach { tank ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(tank.name, color = GridOffWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                                Text("${tank.battles} battles", color = SteelGray, fontSize = 8.sp)
                            }
                            Text(
                                "${tank.winRate}%",
                                color = if (tank.winRate >= 50) NeonGreen else NeonOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
            }
        }
    }
}

@Composable
private fun RadarRow(label: String, pct: Int, icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(icon, null, tint = SteelGray, modifier = Modifier.size(12.dp))
                Text(label, color = SteelGray, fontSize = 9.5.sp, fontFamily = FontFamily.Monospace)
            }
            Text(value, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { pct / 100f },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = NeonOrange,
            trackColor = Color.Black
        )
    }
}

@Composable
private fun MultiplierCard(label: String, value: String, status: String, isPositive: Boolean) {
    TacticalCard {
        Text(label, color = SteelGray, fontSize = 7.5.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, color = if (isPositive) NeonGreen else NeonRed, fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .background((if (isPositive) NeonGreen else NeonRed).copy(alpha = 0.08f), RoundedCornerShape(3.dp))
                .border(1.dp, (if (isPositive) NeonGreen else NeonRed).copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(status.uppercase(), color = if (isPositive) NeonGreen else NeonRed, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}
