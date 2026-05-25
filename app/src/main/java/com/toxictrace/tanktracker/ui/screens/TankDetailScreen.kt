package com.toxictrace.tanktracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.toxictrace.tanktracker.model.TankInfo
import com.toxictrace.tanktracker.ui.components.ComposeMasteryBadge
import com.toxictrace.tanktracker.ui.components.NationBadge
import com.toxictrace.tanktracker.ui.components.TacticalCard
import com.toxictrace.tanktracker.ui.components.TankMiniClassIcon
import com.toxictrace.tanktracker.ui.theme.*

@Composable
fun TankDetailScreen(tank: TankInfo, onBack: () -> Unit) {
    val scroll = rememberScrollState()
    val wrColor = when {
        tank.winRate >= 60 -> NeonGreen
        tank.winRate >= 54 -> Color(0xFF84DFAF)
        tank.winRate >= 50 -> NeonOrange
        else -> NeonRed
    }

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBg).verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().background(DarkSurface)
                .border(bottomBorder = true)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ArrowBack, null, tint = SteelGray)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(tank.name, color = Color.White, fontSize = 14.sp,
                    fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(toRomanTier(tank.tier), color = NeonOrange.copy(0.8f), fontSize = 10.sp,
                        fontStyle = FontStyle.Italic, fontFamily = FontFamily.Monospace)
                    TankMiniClassIcon(tankClass = tank.tankClass)
                    NationBadge(nation = tank.nation)
                    if (tank.isPremium) Text("★", color = Color(0xFFFBBF24), fontSize = 10.sp)
                }
            }
            ComposeMasteryBadge(badge = tank.masteryBadge)
        }

        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Tank image ────────────────────────────────────────────────────
            if (tank.imageUrl != null) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(DarkSurface, RoundedCornerShape(12.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = tank.imageUrl, contentDescription = tank.name,
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // ── Key metrics ───────────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    TankMetricCard("BATTLES", tank.battles.toString(), SteelGray, Icons.Default.SwapHoriz)
                }
                Box(modifier = Modifier.weight(1f)) {
                    TankMetricCard("WIN RATE", "${tank.winRate}%", wrColor, Icons.Default.EmojiEvents)
                }
                Box(modifier = Modifier.weight(1f)) {
                    TankMetricCard("AVG DMG", tank.avgDamage.toString(), NeonRed, Icons.Default.LocalFireDepartment)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    TankMetricCard("SURVIVAL", "${tank.survivalRate}%", NeonGreen, Icons.Default.Shield)
                }
                Box(modifier = Modifier.weight(1f)) {
                    TankMetricCard("ACCURACY", "${tank.accuracy}%", Color(0xFF38BDF8), Icons.Default.GpsFixed)
                }
                Box(modifier = Modifier.weight(1f)) {
                    TankMetricCard("AVG XP", tank.avgXP.toString(), Color(0xFFB4F04A), Icons.Default.Star)
                }
            }

            // ── Progress bars ─────────────────────────────────────────────────
            TacticalCard {
                Text("PERFORMANCE BREAKDOWN", color = GridOffWhite, fontSize = 10.sp,
                    fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(12.dp))
                TankProgressBar("Win Rate",     "${tank.winRate}%",     tank.winRate / 100,     wrColor)
                Spacer(modifier = Modifier.height(8.dp))
                TankProgressBar("Accuracy",     "${tank.accuracy}%",    tank.accuracy / 100,    Color(0xFF38BDF8))
                Spacer(modifier = Modifier.height(8.dp))
                TankProgressBar("Survival",     "${tank.survivalRate}%",tank.survivalRate / 100, NeonGreen)
                Spacer(modifier = Modifier.height(8.dp))
                val kd = (tank.avgFrags / 1.0).coerceIn(0.0, 1.0)
                TankProgressBar("Avg Frags",    tank.avgFrags.toString(), kd,                  NeonRed)
            }

            // ── Extended stats ────────────────────────────────────────────────
            TacticalCard {
                Text("EXTENDED STATS", color = GridOffWhite, fontSize = 10.sp,
                    fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(10.dp))
                TankStatRow("Avg Assist Damage", tank.avgAssist.toString(),  Icons.Default.Radar,     Color(0xFFB4F04A))
                HLine()
                TankStatRow("Avg Spotted",       tank.avgSpotted.toString(), Icons.Default.Visibility, Color(0xFF38BDF8))
                HLine()
                TankStatRow("Avg Blocked",       tank.avgBlocked.toString(), Icons.Default.Security,   Color(0xFF94A3B8))
                HLine()
                TankStatRow("Avg Frags/Battle",  tank.avgFrags.toString(),   Icons.Default.FlashOn,    NeonRed)
                HLine()
                TankStatRow("Avg XP/Battle",     tank.avgXP.toString(),      Icons.Default.Star,       Color(0xFFB4F04A))
            }

            // ── Damage breakdown ──────────────────────────────────────────────
            TacticalCard {
                Text("DAMAGE SOURCES", color = GridOffWhite, fontSize = 10.sp,
                    fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(10.dp))
                val total = (tank.avgDamage + tank.avgAssist + tank.avgBlocked).toFloat().coerceAtLeast(1f)
                DmgBar("Direct",   tank.avgDamage,  tank.avgDamage / total,  NeonRed)
                Spacer(modifier = Modifier.height(6.dp))
                DmgBar("Assist",   tank.avgAssist,  tank.avgAssist / total,  Color(0xFFB4F04A))
                Spacer(modifier = Modifier.height(6.dp))
                DmgBar("Blocked",  tank.avgBlocked, tank.avgBlocked / total, Color(0xFF94A3B8))
            }

            // ── Mastery progress ──────────────────────────────────────────────
            TacticalCard {
                Text("MASTERY & MARKS", color = GridOffWhite, fontSize = 10.sp,
                    fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) { MasteryBadgeDisplay("III", tank.masteryBadge.ordinal >= 1, Color(0xFFD97706)) }
                    Box(modifier = Modifier.weight(1f)) { MasteryBadgeDisplay("II",  tank.masteryBadge.ordinal >= 2, Color(0xFF9CA3AF)) }
                    Box(modifier = Modifier.weight(1f)) { MasteryBadgeDisplay("I",   tank.masteryBadge.ordinal >= 3, Color(0xFFD1D5DB)) }
                    Box(modifier = Modifier.weight(1f)) { MasteryBadgeDisplay("ACE", tank.masteryBadge.ordinal >= 4, Color(0xFFFBBF24)) }
                }
                if (tank.marksOfExcellence > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Marks of Excellence:", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        repeat(tank.marksOfExcellence) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFBBF24), modifier = Modifier.size(14.dp))
                        }
                        repeat(3 - tank.marksOfExcellence) {
                            Icon(Icons.Default.StarBorder, null, tint = DarkCardBorder, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
private fun TankMetricCard(label: String, value: String, color: Color, icon: ImageVector) {
    TacticalCard(borderColor = color.copy(0.2f)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = SteelGray, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
            Icon(icon, null, tint = color.copy(0.7f), modifier = Modifier.size(11.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun TankProgressBar(label: String, value: String, progress: Double, color: Color) {
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
private fun TankStatRow(label: String, value: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(13.dp))
            Text(label, color = SteelGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
        Text(value, color = GridOffWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun DmgBar(label: String, value: Int, fraction: Float, color: Color) {
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
private fun MasteryBadgeDisplay(label: String, achieved: Boolean, color: Color) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(if (achieved) color.copy(0.1f) else Color.Black.copy(0.3f), RoundedCornerShape(6.dp))
            .border(1.dp, if (achieved) color.copy(0.4f) else DarkCardBorder, RoundedCornerShape(6.dp))
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (achieved) color else SteelGray.copy(0.4f),
            fontSize = 10.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun HLine() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkCardBorder))
}

// Extension to add bottom border
@Composable
private fun Modifier.border(bottomBorder: Boolean): Modifier =
    if (bottomBorder) this.border(1.dp, DarkCardBorder, RoundedCornerShape(0.dp)) else this

private fun toRomanTier(tier: Int) = when (tier) {
    10 -> "X"; 9 -> "IX"; 8 -> "VIII"; 7 -> "VII"; 6 -> "VI"
    5 -> "V"; 4 -> "IV"; 3 -> "III"; 2 -> "II"; 1 -> "I"; else -> "?"
}
