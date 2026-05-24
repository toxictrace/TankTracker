package com.toxictrace.tanktracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.toxictrace.tanktracker.ui.components.ComposeMasteryBadge
import com.toxictrace.tanktracker.ui.components.NationBadge
import com.toxictrace.tanktracker.ui.components.TacticalCard
import com.toxictrace.tanktracker.ui.components.TankMiniClassIcon
import com.toxictrace.tanktracker.ui.theme.*

@Composable
fun HangarScreen(player: PlayerProfile, onSearchNewPlayer: () -> Unit) {
    val scrollState = rememberScrollState()
    val showcaseTanks = remember(player.tanks) { player.tanks.filter { it.tier >= 9 } }
    var activeIndex by remember { mutableStateOf(0) }
    val activeTank = showcaseTanks.getOrNull(activeIndex)

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBg).verticalScroll(scrollState).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("HANGAR PARKING", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                Text("Showcase & Profile", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }
            Row(
                modifier = Modifier
                    .background(DarkSurface, RoundedCornerShape(6.dp))
                    .border(1.dp, NeonOrange.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
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
        TacticalCard(borderColor = NeonOrange.copy(alpha = 0.2f)) {
            Text("ACCOUNT SUMMARY", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatItem("BATTLES", player.battlesPlayed.toString())
                StatItem("WIN RATE", "${player.winRatePct}%")
                StatItem("AVG DMG", player.avgDamage.toString())
                StatItem("WN8", player.wn8Value.toString())
            }
        }

        // Top tier showcase
        if (activeTank != null) {
            TacticalCard(borderColor = NeonOrange.copy(alpha = 0.3f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ELITE ASSET SHOWCASE", color = NeonOrange, fontSize = 8.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                    if (showcaseTanks.size > 1) {
                        Row(
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(4.dp))
                                .clickable { activeIndex = (activeIndex + 1) % showcaseTanks.size }
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("NEXT", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Icon(Icons.Default.ChevronRight, null, tint = SteelGray, modifier = Modifier.size(10.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                if (activeTank.imageUrl != null) {
                    AsyncImage(
                        model = activeTank.imageUrl,
                        contentDescription = activeTank.name,
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        NationBadge(nation = activeTank.nation)
                        TankMiniClassIcon(tankClass = activeTank.tankClass, modifier = Modifier.size(11.dp))
                        ComposeMasteryBadge(badge = activeTank.masteryBadge)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(activeTank.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatItem("BATTLES", activeTank.battles.toString())
                        StatItem("WIN RATE", "${activeTank.winRate}%")
                        StatItem("AVG DMG", activeTank.avgDamage.toString())
                    }
                }
            }
        }

        // Clan block
        if (player.clanTag.isNotEmpty()) {
            TacticalCard {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0x3310B981), RoundedCornerShape(6.dp))
                            .border(1.dp, NeonGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Hardware, null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                    }
                    Column {
                        Text("CLAN", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text(player.clanName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("[${player.clanTag}]", color = NeonOrange, fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                }
            }
        }

        // All tanks count by tier
        TacticalCard {
            Text("GARAGE BREAKDOWN", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            val byTier = player.tanks.groupBy { it.tier }.toSortedMap(reverseOrder())
            byTier.forEach { (tier, list) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tier ${toRoman2(tier)}", color = GridOffWhite, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("${list.size} tanks", color = SteelGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = SteelGray, fontSize = 7.5.sp, fontFamily = FontFamily.Monospace)
        Text(value, color = GridOffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

private fun toRoman2(tier: Int) = when (tier) {
    10 -> "X"; 9 -> "IX"; 8 -> "VIII"; 7 -> "VII"; 6 -> "VI"
    5 -> "V"; 4 -> "IV"; 3 -> "III"; 2 -> "II"; 1 -> "I"; else -> tier.toString()
}
