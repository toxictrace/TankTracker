package com.toxictrace.tanktracker.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toxictrace.tanktracker.api.PlayerSearchItem
import com.toxictrace.tanktracker.ui.theme.*
import com.toxictrace.tanktracker.viewmodel.UiState

@Composable
fun SearchScreen(
    uiState: UiState,
    onSearch: (String) -> Unit,
    onSelectPlayer: (Long) -> Unit
) {
    var query by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF0A0C0E), DarkBg, Color(0xFF111418)))
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⬡", color = NeonOrange, fontSize = 48.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Text("TANK TRACKER", color = Color.White, fontSize = 22.sp,
                    fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, letterSpacing = 4.sp)
                Text("EU SERVER STATISTICS", color = SteelGray, fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter player nickname...", color = SteelGray, fontSize = 13.sp, fontFamily = FontFamily.Monospace) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = NeonOrange) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = NeonOrange, unfocusedBorderColor = DarkCardBorder,
                    focusedContainerColor = DarkSurface, unfocusedContainerColor = DarkSurface,
                    cursorColor = NeonOrange
                ),
                shape = RoundedCornerShape(8.dp), singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { if (query.length >= 3) onSearch(query) }),
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 14.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { if (query.length >= 3) onSearch(query) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonOrange),
                shape = RoundedCornerShape(8.dp),
                enabled = query.length >= 3 && uiState !is UiState.Loading
            ) {
                Text("SEARCH PILOT", color = Color.Black, fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace, fontSize = 13.sp, letterSpacing = 1.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (uiState) {
                is UiState.Loading -> {
                    // Skeleton loading
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(4) { SkeletonRow() }
                    }
                }
                is UiState.SearchResults -> {
                    Text("SELECT PLAYER", color = SteelGray, fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(uiState.results) { player ->
                            PlayerResultRow(player = player, onClick = { onSelectPlayer(player.accountId) })
                        }
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(NeonRed.copy(0.08f), RoundedCornerShape(8.dp))
                            .border(1.dp, NeonRed.copy(0.3f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) { Text(uiState.message, color = NeonRed, fontSize = 12.sp, fontFamily = FontFamily.Monospace) }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun PlayerResultRow(player: PlayerSearchItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(DarkSurface, RoundedCornerShape(8.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(player.nickname, color = Color.White, fontSize = 14.sp,
            fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Text("#${player.accountId}", color = SteelGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun SkeletonRow() {
    val inf = rememberInfiniteTransition(label = "shimmer")
    val x by inf.animateFloat(
        initialValue = -300f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "x"
    )
    val shimmer = Brush.linearGradient(
        colors = listOf(DarkSurface, DarkSurfaceLighter, DarkSurface),
        start = Offset(x, 0f), end = Offset(x + 300f, 0f)
    )
    Box(
        modifier = Modifier.fillMaxWidth().height(48.dp)
            .background(shimmer, RoundedCornerShape(8.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
    )
}
