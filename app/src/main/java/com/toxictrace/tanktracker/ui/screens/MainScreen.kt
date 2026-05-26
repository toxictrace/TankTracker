package com.toxictrace.tanktracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toxictrace.tanktracker.model.PlayerProfile
import com.toxictrace.tanktracker.ui.theme.*

private data class NavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun MainScreen(profile: PlayerProfile, onSearchNewPlayer: () -> Unit) {
    val navItems = listOf(
        NavItem("Home",    Icons.Default.Home,                     "home"),
        NavItem("Armory",  Icons.AutoMirrored.Filled.List,         "armory"),
        NavItem("Session", Icons.Default.Timer,                    "session"),
        NavItem("Tactics", Icons.AutoMirrored.Filled.TrendingUp,   "tactics"),
        NavItem("Compare", Icons.Default.CompareArrows,            "compare"),
        NavItem("Hangar",  Icons.Default.Garage,                   "hangar")
    )
    var route by remember { mutableStateOf("home") }

    Scaffold(
        containerColor = DarkBg,
        bottomBar = {
            NavigationBar(containerColor = DarkSurface, tonalElevation = 0.dp) {
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = route == item.route,
                        onClick = { route = item.route },
                        icon = { Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(20.dp)) },
                        label = {
                            Text(item.label, fontSize = 7.sp,
                                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonOrange,
                            selectedTextColor = NeonOrange,
                            unselectedIconColor = SteelGray,
                            unselectedTextColor = SteelGray,
                            indicatorColor = NeonOrange.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkBg)
        ) {
            when (route) {
                "home"    -> HomeScreen(player = profile)
                "armory"  -> ArmoryScreen(tanks = profile.tanks)
                "session" -> SessionScreen(profile = profile)
                "tactics" -> TacticsScreen(player = profile)
                "compare" -> CompareScreen(currentProfile = profile)
                "hangar"  -> HangarScreen(player = profile, onSearchNewPlayer = onSearchNewPlayer)
            }
        }
    }
}
