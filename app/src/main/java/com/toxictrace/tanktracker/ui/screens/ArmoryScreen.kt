package com.toxictrace.tanktracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArmoryScreen(tanks: List<TankInfo>) {
    var searchStr by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf<TankClass?>(null) }

    val filtered = remember(tanks, searchStr, selectedClass) {
        tanks.filter {
            val matchSearch = it.name.contains(searchStr, ignoreCase = true)
            val matchClass = selectedClass == null || it.tankClass == selectedClass
            matchSearch && matchClass
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBg).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("TANK CATALOGUE", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                Text("Armored Inventory", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("${filtered.size} / ${tanks.size}", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
        }

        TacticalCard {
            TextField(
                value = searchStr,
                onValueChange = { searchStr = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search vehicles...", color = Color(0x7FFFFFFF), fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Black.copy(alpha = 0.6f),
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.6f),
                    focusedIndicatorColor = NeonOrange.copy(alpha = 0.5f),
                    unfocusedIndicatorColor = DarkCardBorder
                ),
                shape = RoundedCornerShape(6.dp),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = SteelGray, modifier = Modifier.size(16.dp)) },
                textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = selectedClass == null,
                    onClick = { selectedClass = null },
                    label = { Text("ALL", fontSize = 9.sp, fontFamily = FontFamily.Monospace) }
                )
                TankClass.values().forEach { tc ->
                    FilterChip(
                        selected = selectedClass == tc,
                        onClick = { selectedClass = if (selectedClass == tc) null else tc },
                        label = { Text(tc.name.take(2), fontSize = 9.sp, fontFamily = FontFamily.Monospace) }
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                Text("NO VEHICLES FOUND", color = SteelGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                items(filtered) { tank ->
                    TankCard(tank = tank)
                }
            }
        }
    }
}

@Composable
private fun TankCard(tank: TankInfo) {
    val winRateCol = when {
        tank.winRate >= 60.0 -> Color(0xFFA3FFA3)
        tank.winRate >= 54.0 -> Color(0xFF84DFAF)
        tank.winRate >= 49.0 -> Color(0xFFFFFF33)
        else -> Color(0xFFFF4C4C)
    }

    TacticalCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        toRoman(tank.tier),
                        color = NeonOrange.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Monospace
                    )
                    TankMiniClassIcon(tankClass = tank.tankClass, modifier = Modifier.size(10.dp))
                    NationBadge(nation = tank.nation)
                    ComposeMasteryBadge(badge = tank.masteryBadge)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(tank.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                Text("${tank.battles} battles", color = Color(0xFF718294), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                if (tank.marksOfExcellence > 0) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Marks:", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        repeat(tank.marksOfExcellence) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFBBF24), modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }

            // Tank image if available
            if (tank.imageUrl != null) {
                AsyncImage(
                    model = tank.imageUrl,
                    contentDescription = tank.name,
                    modifier = Modifier.size(80.dp, 48.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("WIN RATE", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                Text("${tank.winRate}%", color = winRateCol, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(6.dp))
                Text("AVG DMG", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                Text(tank.avgDamage.toString(), color = GridOffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

private fun toRoman(tier: Int) = when (tier) {
    10 -> "X"; 9 -> "IX"; 8 -> "VIII"; 7 -> "VII"; 6 -> "VI"
    5 -> "V"; 4 -> "IV"; 3 -> "III"; 2 -> "II"; 1 -> "I"; else -> tier.toString()
}
