package com.toxictrace.tanktracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toxictrace.tanktracker.model.PlayerProfile
import com.toxictrace.tanktracker.model.WN8Ratings
import com.toxictrace.tanktracker.ui.components.TacticalCard
import com.toxictrace.tanktracker.ui.theme.*

@Composable
fun HomeScreen(player: PlayerProfile) {
    val scrollState = rememberScrollState()
    val wn8Color = getWn8ComposeColor(player.wn8Value)
    val wn8Label = WN8Ratings.getRatingLabel(player.wn8Value)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Identity ribbon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(DarkSurfaceLighter, Color.Transparent)),
                    RoundedCornerShape(8.dp)
                )
                .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = player.nickname,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    if (player.clanTag.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(NeonOrange.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .border(1.dp, NeonOrange.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("[${player.clanTag}]", color = NeonOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (player.clanName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(player.clanName, color = SteelGray, fontSize = 10.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("GLOBAL PR", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, null, tint = NeonOrange, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        "#${player.globalRating}",
                        color = NeonOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // WN8 panel
        TacticalCard(borderColor = wn8Color.copy(alpha = 0.4f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { player.wn8Value.toFloat() / 3500f },
                        modifier = Modifier.size(80.dp),
                        color = wn8Color,
                        strokeWidth = 5.dp,
                        trackColor = Color.Black
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("WN8", color = SteelGray, fontSize = 10.sp)
                        Text(
                            player.wn8Value.toString(),
                            color = wn8Color,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Column {
                    Box(
                        modifier = Modifier
                            .background(wn8Color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .border(1.dp, wn8Color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(wn8Label.uppercase(), color = wn8Color, fontSize = 10.sp,
                            fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("WIN RATIO", color = SteelGray, fontSize = 8.sp)
                            Text("${player.winRatePct}%", color = GridOffWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("AVG XP", color = SteelGray, fontSize = 8.sp)
                            Text("${player.avgXP} XP", color = NeonGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Stat tiles
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                PrimaryMetricTile("TOTAL BATTLES", player.battlesPlayed.toString(), "Logged Battles", Icons.Default.SwapHoriz, NeonOrange)
            }
            Box(modifier = Modifier.weight(1f)) {
                PrimaryMetricTile("FIREPOWER", player.avgDamage.toString(), "Avg Damage", Icons.Default.Star, NeonRed)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                PrimaryMetricTile("K/D RATIO", player.kdRatio.toString(), "Kill / Death", Icons.Default.TrendingUp, NeonOrange)
            }
            Box(modifier = Modifier.weight(1f)) {
                PrimaryMetricTile("SURVIVAL", "${player.survivalRate}%", "Survival Rate", Icons.Default.Shield, NeonGreen)
            }
        }

        // Accuracy bar
        TacticalCard {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("SHELL ACCURACY", color = SteelGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Text("${player.accuracyPct}%", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { player.accuracyPct.toFloat() / 100f },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = NeonGreen,
                trackColor = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("0%", color = SteelGray, fontSize = 7.sp)
                Text("100%", color = SteelGray, fontSize = 7.sp)
            }
        }

        // Tanks played summary
        TacticalCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("GARAGE SIZE", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    Text("${player.tanks.size} vehicles", color = GridOffWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("TOP TIER", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    val topTier = player.tanks.maxOfOrNull { it.tier } ?: 0
                    Text("Tier $topTier", color = NeonOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun PrimaryMetricTile(
    label: String,
    value: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    TacticalCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(14.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, color = GridOffWhite, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
        Text(desc, color = SteelGray, fontSize = 8.sp)
    }
}
