package com.toxictrace.tanktracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.toxictrace.tanktracker.model.TankClass
import com.toxictrace.tanktracker.model.TankInfo
import com.toxictrace.tanktracker.ui.components.ComposeMasteryBadge
import com.toxictrace.tanktracker.ui.components.NationBadge
import com.toxictrace.tanktracker.ui.components.TacticalCard
import com.toxictrace.tanktracker.ui.components.TankMiniClassIcon
import com.toxictrace.tanktracker.ui.theme.*

enum class SortMode { BATTLES, WIN_RATE, AVG_DAMAGE, SURVIVAL }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArmoryScreen(tanks: List<TankInfo>) {
    var search by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf<TankClass?>(null) }
    var sortMode by remember { mutableStateOf(SortMode.BATTLES) }
    var showPremiumOnly by remember { mutableStateOf(false) }

    val filtered = remember(tanks, search, selectedClass, sortMode, showPremiumOnly) {
        tanks
            .filter {
                val ms = it.name.contains(search, ignoreCase = true)
                val mc = selectedClass == null || it.tankClass == selectedClass
                val mp = !showPremiumOnly || it.isPremium
                ms && mc && mp
            }
            .sortedByDescending {
                when (sortMode) {
                    SortMode.BATTLES    -> it.battles.toDouble()
                    SortMode.WIN_RATE   -> it.winRate
                    SortMode.AVG_DAMAGE -> it.avgDamage.toDouble()
                    SortMode.SURVIVAL   -> it.survivalRate
                }
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBg).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("TANK CATALOGUE", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                Text("Armored Inventory", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(0.3f), RoundedCornerShape(4.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) { Text("${filtered.size} / ${tanks.size}", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace) }
        }

        // Search
        TacticalCard {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search vehicles...", color = SteelGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = NeonOrange.copy(0.5f), unfocusedBorderColor = DarkCardBorder,
                    focusedContainerColor = Color.Black.copy(0.4f), unfocusedContainerColor = Color.Black.copy(0.4f),
                    cursorColor = NeonOrange
                ),
                shape = RoundedCornerShape(6.dp),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = SteelGray, modifier = Modifier.size(16.dp)) },
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Class filters
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                FilterChip(selected = selectedClass == null, onClick = { selectedClass = null },
                    label = { Text("ALL", fontSize = 8.sp, fontFamily = FontFamily.Monospace) })
                TankClass.values().forEach { tc ->
                    FilterChip(selected = selectedClass == tc, onClick = { selectedClass = if (selectedClass == tc) null else tc },
                        label = { Text(tc.name.take(2), fontSize = 8.sp, fontFamily = FontFamily.Monospace) })
                }
                Spacer(modifier = Modifier.weight(1f))
                FilterChip(selected = showPremiumOnly, onClick = { showPremiumOnly = !showPremiumOnly },
                    label = { Text("★", fontSize = 10.sp) })
            }

            // Sort
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("SORT:", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.align(Alignment.CenterVertically))
                SortMode.values().forEach { mode ->
                    val label = when (mode) {
                        SortMode.BATTLES -> "BTL"; SortMode.WIN_RATE -> "WR"
                        SortMode.AVG_DAMAGE -> "DMG"; SortMode.SURVIVAL -> "SRV"
                    }
                    FilterChip(selected = sortMode == mode, onClick = { sortMode = mode },
                        label = { Text(label, fontSize = 8.sp, fontFamily = FontFamily.Monospace) })
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Text("NO VEHICLES FOUND", color = SteelGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.fillMaxSize()) {
                items(filtered) { tank -> TankCard(tank) }
            }
        }
    }
}

@Composable
private fun TankCard(tank: TankInfo) {
    val wrColor = when {
        tank.winRate >= 60 -> Color(0xFFA3FFA3)
        tank.winRate >= 54 -> Color(0xFF84DFAF)
        tank.winRate >= 50 -> NeonOrange
        else -> NeonRed
    }
    val premiumBorder = if (tank.isPremium) Color(0xFFFBBF24).copy(0.25f) else DarkCardBorder

    TacticalCard(borderColor = premiumBorder) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            // Left: info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(toRoman(tank.tier), color = NeonOrange.copy(0.8f), fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic, fontFamily = FontFamily.Monospace)
                    TankMiniClassIcon(tankClass = tank.tankClass)
                    NationBadge(nation = tank.nation)
                    if (tank.isPremium) Text("★", color = Color(0xFFFBBF24), fontSize = 10.sp)
                    ComposeMasteryBadge(badge = tank.masteryBadge)
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(tank.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                Text("${tank.battles} battles · ${tank.avgXP} avg XP", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }

            // Center: image
            if (tank.imageUrl != null) {
                AsyncImage(model = tank.imageUrl, contentDescription = tank.name,
                    modifier = Modifier.size(80.dp, 44.dp), contentScale = ContentScale.Fit)
                Spacer(modifier = Modifier.width(6.dp))
            }

            // Right: stats
            Column(horizontalAlignment = Alignment.End) {
                Text("WIN RATE", color = SteelGray, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                Text("${tank.winRate}%", color = wrColor, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(4.dp))
                Text("AVG DMG", color = SteelGray, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                Text(tank.avgDamage.toString(), color = GridOffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(4.dp))
                Text("SURVIVAL", color = SteelGray, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                Text("${tank.survivalRate}%", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

private fun toRoman(tier: Int) = when (tier) {
    10 -> "X"; 9 -> "IX"; 8 -> "VIII"; 7 -> "VII"; 6 -> "VI"
    5 -> "V"; 4 -> "IV"; 3 -> "III"; 2 -> "II"; 1 -> "I"; else -> "?"
}
