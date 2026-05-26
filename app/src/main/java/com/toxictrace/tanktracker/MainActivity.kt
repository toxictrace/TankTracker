package com.toxictrace.tanktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toxictrace.tanktracker.auth.AuthManager
import com.toxictrace.tanktracker.ui.screens.LoginScreen
import com.toxictrace.tanktracker.ui.screens.MainScreen
import com.toxictrace.tanktracker.ui.screens.SearchScreen
import com.toxictrace.tanktracker.ui.theme.DarkBg
import com.toxictrace.tanktracker.ui.theme.NeonOrange
import com.toxictrace.tanktracker.ui.theme.SteelGray
import com.toxictrace.tanktracker.ui.theme.TankTrackerTheme
import com.toxictrace.tanktracker.viewmodel.PlayerViewModel
import com.toxictrace.tanktracker.viewmodel.UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TankTrackerTheme {
                TankTrackerApp()
            }
        }
    }
}

@Composable
private fun TankTrackerApp() {
    val context = LocalContext.current
    val vm: PlayerViewModel = viewModel()
    val uiState by vm.uiState.collectAsState()

    // On first launch — auto-load if logged in
    LaunchedEffect(Unit) {
        if (AuthManager.isLoggedIn(context)) {
            val accountId = AuthManager.getAccountId(context)
            if (accountId != -1L) vm.loadProfile(accountId)
        }
    }

    when (val state = uiState) {
        is UiState.Success -> {
            MainScreen(
                profile = state.profile,
                onSearchNewPlayer = { vm.resetToIdle() }
            )
        }

        is UiState.Loading -> {
            // Loading splash
            Box(
                modifier = Modifier.fillMaxSize().background(DarkBg),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("⬡", color = NeonOrange, fontSize = 48.sp)
                    CircularProgressIndicator(color = NeonOrange, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                    Text("LOADING PROFILE...", color = SteelGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        is UiState.Idle -> {
            // Show login if not logged in, else search
            if (AuthManager.isLoggedIn(context)) {
                // Already logged in but state reset — reload
                LaunchedEffect(Unit) {
                    val accountId = AuthManager.getAccountId(context)
                    if (accountId != -1L) vm.loadProfile(accountId)
                }
                Box(modifier = Modifier.fillMaxSize().background(DarkBg), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonOrange, strokeWidth = 2.dp)
                }
            } else {
                LoginScreen(
                    onLoginSuccess = { accountId ->
                        vm.loadProfile(accountId)
                    },
                    onSkip = { vm.resetToSearch() }
                )
            }
        }

        is UiState.SearchResults, is UiState.Error -> {
            SearchScreen(
                uiState = state,
                onSearch = { vm.searchPlayers(it) },
                onSelectPlayer = { vm.loadProfile(it) }
            )
        }
    }
}
