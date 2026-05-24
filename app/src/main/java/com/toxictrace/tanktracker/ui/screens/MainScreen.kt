package com.toxictrace.tanktracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toxictrace.tanktracker.model.PlayerProfile
import com.toxictrace.tanktracker.ui.theme.*

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun MainScreen(profile: PlayerProfile, onSearchNewPlayer: () -> Unit) {
    val navItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, "home"),
        BottomNavItem("Armory", Icons.Default.List, "armory"),
        BottomNavItem("Tactics", Icons.Default.TrendingUp, "tactics"),
        BottomNavItem("Hangar", Icons.Default.Garage, "hangar")
    )
    var selectedRoute by remember { mutableStateOf("home") }

    Scaffold(
        containerColor = DarkBg,
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 0.dp
            ) {
                navItems.forEach { item ->
                    NavigationBarItem(
                        selected = selectedRoute == item.route,
                        onClick = { selectedRoute = item.route },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = {
                            Text(
                                item.label,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkBg)
        ) {
            when (selectedRoute) {
                "home"    -> HomeScreen(player = profile)
                "armory"  -> ArmoryScreen(tanks = profile.tanks)
                "tactics" -> TacticsScreen(player = profile)
                "hangar"  -> HangarScreen(player = profile, onSearchNewPlayer = onSearchNewPlayer)
            }
        }
    }
}
