package com.toxictrace.tanktracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toxictrace.tanktracker.model.PlayerProfile
import com.toxictrace.tanktracker.ui.components.TacticalCard
import com.toxictrace.tanktracker.ui.theme.*
import com.toxictrace.tanktracker.viewmodel.CompareViewModel
import com.toxictrace.tanktracker.viewmodel.CompareState

@Composable
fun CompareScreen(currentProfile: PlayerProfile) {
    val vm: CompareViewModel = viewModel()
    val state by vm.state.collectAsState()
    var query by remember { mutableStateOf("") }
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier.fillMaxSize().background(DarkBg).verticalScroll(scroll).padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text("PLAYER COMPARE", color = SteelGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            Text("Head-to-Head Analysis", color = Color.White, fontSize = 16.sp,
                fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
        }

        // Search opponent
        TacticalCard {
            Text("SEARCH OPPONENT", color = SteelGray, fontSize = 8.sp,
                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query, onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Nickname...", color = SteelGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonOrange.copy(0.5f), unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = Color.Black.copy(0.4f), unfocusedContainerColor = Color.Black.copy(0.4f),
                        cursorColor = NeonOrange
                    ),
                    shape = RoundedCornerShape(6.dp), singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { if (query.length >= 3) vm.loadOpponent(query) }),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                )
                Button(
                    onClick = { if (query.length >= 3) vm.loadOpponent(query) },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonOrange),
                    shape = RoundedCornerShape(6.dp),
                    enabled = query.length >= 3 && state !is CompareState.Loading,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    if (state is CompareState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Search, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        when (val s = state) {
            is CompareState.Error -> {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(NeonRed.copy(0.08f), RoundedCornerShape(8.dp))
                        .border(1.dp, NeonRed.copy(0.3f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) { Text(s.message, color = NeonRed, fontSize = 11.sp, fontFamily = FontFamily.Monospace) }
            }

            is CompareState.Ready -> {
                val opponent = s.profile
                CompareContent(player1 = currentProfile, player2 = opponent)
            }

            is CompareState.Idle -> {
                // Placeholder
                TacticalCard {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CompareArrows, null, tint = SteelGray, modifier = Modifier.size(32.dp))
                        Text("SEARCH AN OPPONENT", color = SteelGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("Enter a nickname to compare stats", color = SteelGray.copy(0.5f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun CompareContent(player1: PlayerProfile, player2: PlayerProfile) {
    // ── Name headers ──────────────────────────────────────────────────────────
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PlayerNameCard(player1, NeonOrange, modifier = Modifier.weight(1f))
        PlayerNameCard(player2, Color(0xFF38BDF8), modifier = Modifier.weight(1f))
    }

    // ── Stat comparisons ──────────────────────────────────────────────────────
    TacticalCard {
        Text("STATISTICS COMPARISON", color = GridOffWhite, fontSize = 10.sp,
            fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(12.dp))

        CompareRow("WN8",        player1.wn8Value.toString(),      player2.wn8Value.toString(),
            player1.wn8Value.toDouble(),  player2.wn8Value.toDouble(),  higher = true)
        HDivider()
        CompareRow("WIN RATE",   "${player1.winRatePct}%",         "${player2.winRatePct}%",
            player1.winRatePct,           player2.winRatePct,           higher = true)
        HDivider()
        CompareRow("AVG DAMAGE", player1.avgDamage.toString(),     player2.avgDamage.toString(),
            player1.avgDamage.toDouble(), player2.avgDamage.toDouble(), higher = true)
        HDivider()
        CompareRow("K/D",        player1.kdRatio.toString(),       player2.kdRatio.toString(),
            player1.kdRatio,              player2.kdRatio,              higher = true)
        HDivider()
        CompareRow("SURVIVAL",   "${player1.survivalRate}%",       "${player2.survivalRate}%",
            player1.survivalRate,         player2.survivalRate,         higher = true)
        HDivider()
        CompareRow("ACCURACY",   "${player1.accuracyPct}%",        "${player2.accuracyPct}%",
            player1.accuracyPct,          player2.accuracyPct,          higher = true)
        HDivider()
        CompareRow("AVG ASSIST", player1.avgAssist.toString(),     player2.avgAssist.toString(),
            player1.avgAssist.toDouble(), player2.avgAssist.toDouble(), higher = true)
        HDivider()
        CompareRow("AVG SPOTTED",player1.avgSpotted.toString(),    player2.avgSpotted.toString(),
            player1.avgSpotted,           player2.avgSpotted,           higher = true)
        HDivider()
        CompareRow("BATTLES",    player1.battlesPlayed.toString(), player2.battlesPlayed.toString(),
            player1.battlesPlayed.toDouble(), player2.battlesPlayed.toDouble(), higher = true)
    }

    // ── Radar-style score ─────────────────────────────────────────────────────
    val p1Score = calcScore(player1)
    val p2Score = calcScore(player2)
    val total = (p1Score + p2Score).coerceAtLeast(1.0)

    TacticalCard {
        Text("OVERALL SCORE", color = GridOffWhite, fontSize = 10.sp,
            fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(player1.nickname, color = NeonOrange, fontSize = 11.sp,
                    fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
                Text("%.0f".format(p1Score), color = NeonOrange, fontSize = 22.sp,
                    fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }
            Text("VS", color = SteelGray, fontSize = 12.sp,
                fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(player2.nickname, color = Color(0xFF38BDF8), fontSize = 11.sp,
                    fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center)
                Text("%.0f".format(p2Score), color = Color(0xFF38BDF8), fontSize = 22.sp,
                    fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        // Split bar
        val p1Frac = (p1Score / total).toFloat().coerceIn(0f, 1f)
        Row(modifier = Modifier.fillMaxWidth().height(8.dp).background(Color.Black, RoundedCornerShape(4.dp))) {
            Box(modifier = Modifier.fillMaxHeight().weight(p1Frac).background(NeonOrange, RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)))
            Box(modifier = Modifier.fillMaxHeight().weight((1f - p1Frac).coerceAtLeast(0.001f)).background(Color(0xFF38BDF8), RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)))
        }
        Spacer(modifier = Modifier.height(6.dp))
        val winner = if (p1Score > p2Score) player1.nickname else player2.nickname
        val winColor = if (p1Score > p2Score) NeonOrange else Color(0xFF38BDF8)
        Text("$winner leads", color = winColor, fontSize = 9.sp,
            fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }

    // ── Garage comparison ─────────────────────────────────────────────────────
    TacticalCard {
        Text("GARAGE COMPARISON", color = GridOffWhite, fontSize = 10.sp,
            fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(10.dp))
        CompareRow("VEHICLES",   player1.tanks.size.toString(),    player2.tanks.size.toString(),
            player1.tanks.size.toDouble(), player2.tanks.size.toDouble(), higher = true)
        HDivider()
        val p1T10 = player1.tanks.count { it.tier == 10 }
        val p2T10 = player2.tanks.count { it.tier == 10 }
        CompareRow("TIER X",     p1T10.toString(),                 p2T10.toString(),
            p1T10.toDouble(),             p2T10.toDouble(),             higher = true)
        HDivider()
        val p1Prem = player1.tanks.count { it.isPremium }
        val p2Prem = player2.tanks.count { it.isPremium }
        CompareRow("PREMIUM",    p1Prem.toString(),                p2Prem.toString(),
            p1Prem.toDouble(),            p2Prem.toDouble(),            higher = true)
    }
}

@Composable
private fun PlayerNameCard(profile: PlayerProfile, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(color.copy(0.08f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(profile.nickname, color = color, fontSize = 12.sp,
                fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            if (profile.clanTag.isNotEmpty()) {
                Text("[${profile.clanTag}]", color = color.copy(0.7f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
            Text("${profile.battlesPlayed} btl", color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun CompareRow(
    label: String,
    val1: String, val2: String,
    num1: Double, num2: Double,
    higher: Boolean
) {
    val p1Wins = if (higher) num1 > num2 else num1 < num2
    val p2Wins = if (higher) num2 > num1 else num2 < num1
    val p1Color = when { p1Wins -> NeonGreen; p2Wins -> NeonRed; else -> GridOffWhite }
    val p2Color = when { p2Wins -> NeonGreen; p1Wins -> NeonRed; else -> GridOffWhite }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(val1, color = p1Color, fontSize = 12.sp, fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
        Text(label, color = SteelGray, fontSize = 8.sp, fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        Text(val2, color = p2Color, fontSize = 12.sp, fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HDivider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkCardBorder))
}

private fun calcScore(p: PlayerProfile): Double =
    p.wn8Value * 0.3 + p.winRatePct * 10 + p.avgDamage * 0.05 +
    p.kdRatio * 50 + p.survivalRate * 2 + p.accuracyPct * 2
