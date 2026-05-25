package com.toxictrace.tanktracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.toxictrace.tanktracker.model.PlayerProfile
import com.toxictrace.tanktracker.model.TankInfo
import com.toxictrace.tanktracker.ui.components.ComposeMasteryBadge
import com.toxictrace.tanktracker.ui.components.NationBadge
import com.toxictrace.tanktracker.ui.components.TacticalCard
import com.toxictrace.tanktracker.ui.components.TankMiniClassIcon
import com.toxictrace.tanktracker.ui.theme.*

@Composable
fun HangarScreen(player: PlayerProfile, onSearchNewPlayer: () -> Unit) {
    val scroll = rememberScrollState()
    val topTanks = remember(player.tanks) {
        player.tanks.filter { it.tier >= 9 }.sortedByDescending { it.battles }
    }
    var activeIndex by remember { mutableStateOf(0) }
    val activeTank = topTanks.getOrNull(activeIndex)

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBg).verticalScroll(scroll).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("HANGAR", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                Text("Showcase & Profile", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }
            Row(
                modifier = Modifier
                    .background(DarkSurface, RoundedCornerShape(6.dp))
                    .border(1.dp, NeonOrange.copy(0.3f), RoundedCornerShape(6.dp))
                    .clickable(onClick = onSearchNewPlayer)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Search, null, tint = NeonOrange, modifier = Modifier.size(14.dp))
                Text("NEW SEARCH", color = NeonOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }

        // Account summary
        TacticalCard(borderColor = NeonOrange.copy(0.2f)) {
            Text("ACCOUNT SUMMARY", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryItem("BATTLES",  player.battlesPlayed.toString())
                SummaryItem("WIN RATE", "${player.winRatePct}%")
                SummaryItem("AVG DMG",  player.avgDamage.toString())
                SummaryItem("WN8",      player.wn8Value.toString())
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SummaryItem("K/D",      player.kdRatio.toString())
                SummaryItem("SURVIVAL", "${player.survivalRate}%")
                SummaryItem("ACCURACY", "${player.accuracyPct}%")
                SummaryItem("AVG XP",   player.avgXP.toString())
            }
        }

        // Elite showcase
        if (activeTank != null) {
            TacticalCard(borderColor = NeonOrange.copy(0.3f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("TIER IX–X SHOWCASE", color = NeonOrange, fontSize = 8.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                    if (topTanks.size > 1) {
                        Row(
                            modifier = Modifier
                                .background(Color.Black.copy(0.3f), RoundedCornerShape(4.dp))
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(4.dp))
                                .clickable { activeIndex = (activeIndex + 1) % topTanks.size }
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("${activeIndex + 1}/${topTanks.size}", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Icon(Icons.Default.ChevronRight, null, tint = SteelGray, modifier = Modifier.size(12.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                ShowcaseTankCard(activeTank)
            }
        }

        // Clan
        if (player.clanTag.isNotEmpty()) {
            TacticalCard {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(40.dp)
                            .background(NeonGreen.copy(0.1f), RoundedCornerShape(6.dp))
                            .border(1.dp, NeonGreen.copy(0.2f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Groups, null, tint = NeonGreen, modifier = Modifier.size(18.dp)) }
                    Column {
                        Text("CLAN", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text(player.clanName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .background(NeonOrange.copy(0.1f), RoundedCornerShape(4.dp))
                            .border(1.dp, NeonOrange.copy(0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) { Text("[${player.clanTag}]", color = NeonOrange, fontSize = 11.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace) }
                }
            }
        }

        // Garage breakdown by tier
        TacticalCard {
            Text("GARAGE BREAKDOWN", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            player.tanks.groupBy { it.tier }.toSortedMap(reverseOrder()).forEach { (tier, list) ->
                val avgWr = list.sumOf { it.winRate * it.battles } / list.sumOf { it.battles }.coerceAtLeast(1)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Tier ${toRoman(tier)}", color = GridOffWhite, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("${list.size} tanks", color = SteelGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("%.1f%%".format(avgWr), color = if (avgWr >= 50) NeonGreen else NeonRed,
                            fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowcaseTankCard(tank: TankInfo) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Color.Black.copy(0.2f), RoundedCornerShape(8.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        if (tank.imageUrl != null) {
            AsyncImage(model = tank.imageUrl, contentDescription = tank.name,
                modifier = Modifier.fillMaxWidth().height(80.dp), contentScale = ContentScale.Fit)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            NationBadge(nation = tank.nation)
            TankMiniClassIcon(tankClass = tank.tankClass)
            ComposeMasteryBadge(badge = tank.masteryBadge)
            if (tank.isPremium) Text("★", color = Color(0xFFFBBF24), fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(tank.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SummaryItem("BATTLES", tank.battles.toString())
            SummaryItem("WIN RATE", "${tank.winRate}%")
            SummaryItem("AVG DMG", tank.avgDamage.toString())
            SummaryItem("SURVIVAL", "${tank.survivalRate}%")
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = SteelGray, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
        Text(value, color = GridOffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

private fun toRoman(tier: Int) = when (tier) {
    10 -> "X"; 9 -> "IX"; 8 -> "VIII"; 7 -> "VII"; 6 -> "VI"
    5 -> "V"; 4 -> "IV"; 3 -> "III"; 2 -> "II"; 1 -> "I"; else -> "?"
}
