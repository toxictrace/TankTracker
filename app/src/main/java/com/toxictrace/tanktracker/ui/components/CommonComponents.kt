package com.toxictrace.tanktracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toxictrace.tanktracker.model.MasteryBadge
import com.toxictrace.tanktracker.model.TankClass
import com.toxictrace.tanktracker.ui.theme.*

@Composable
fun TacticalCard(
    modifier: Modifier = Modifier,
    borderColor: Color = DarkCardBorder,
    backgroundColor: Color = DarkSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp), content = content)
    }
}

@Composable
fun NationBadge(nation: String) {
    val label = when (nation.lowercase()) {
        "ussr" -> "SU"; "germany" -> "DE"; "france" -> "FR"
        "usa" -> "US"; "uk" -> "UK"; "china" -> "CN"
        "japan" -> "JP"; "czech" -> "CZ"; "sweden" -> "SE"
        "poland" -> "PL"; "italy" -> "IT"; else -> nation.take(2).uppercase()
    }
    val bg = when (nation.lowercase()) {
        "ussr" -> Color(0x33EF4444); "germany" -> Color(0x337F8E9C)
        "france" -> Color(0x333B82F6); "usa" -> Color(0x330EA5E9)
        "uk" -> Color(0x338B5CF6); else -> Color(0x331F2937)
    }
    val textCol = when (nation.lowercase()) {
        "ussr" -> Color(0xFFFF8A8A); "germany" -> Color(0xFFE2EDF8)
        "france" -> Color(0xFF93C5FD); "usa" -> Color(0xFF7DD3FC)
        "uk" -> Color(0xFFC4B5FD); else -> Color(0xFF9CA3AF)
    }
    val borderCol = bg.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(4.dp))
            .border(1.dp, borderCol, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(label, color = textCol, fontSize = 9.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun TankMiniClassIcon(tankClass: TankClass, modifier: Modifier = Modifier) {
    val (text, color) = when (tankClass) {
        TankClass.HEAVY -> "◆" to NeonRed
        TankClass.MEDIUM -> "◇" to NeonOrange
        TankClass.LIGHT -> "▲" to NeonGreen
        TankClass.TANK_DESTROYER -> "▼" to Color(0xFF38BDF8)
        TankClass.SPG -> "■" to Color(0xFFD97706)
    }
    Text(text, color = color, fontWeight = FontWeight.Black, fontSize = 13.sp, modifier = modifier)
}

@Composable
fun ComposeMasteryBadge(badge: MasteryBadge) {
    if (badge == MasteryBadge.NONE) return
    val (label, bg, textCol, borderCol) = when (badge) {
        MasteryBadge.ACE    -> Quad("M",   Color(0x40F59E0B), Color(0xFFFBBF24), Color(0x80F59E0B))
        MasteryBadge.FIRST  -> Quad("I",   Color(0x404B5563), Color(0xFFD1D5DB), Color(0x804B5563))
        MasteryBadge.SECOND -> Quad("II",  Color(0x33374151), Color(0xFF9CA3AF), Color(0x60374151))
        MasteryBadge.THIRD  -> Quad("III", Color(0x2278350F), Color(0xFFD97706), Color(0x4078350F))
        MasteryBadge.NONE   -> return
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(3.dp))
            .border(1.dp, borderCol, RoundedCornerShape(3.dp))
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Text(label, fontSize = 9.sp, color = textCol, fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
    }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
