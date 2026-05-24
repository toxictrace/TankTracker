package com.toxictrace.tanktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toxictrace.tanktracker.ui.screens.MainScreen
import com.toxictrace.tanktracker.ui.screens.SearchScreen
import com.toxictrace.tanktracker.ui.theme.DarkBg
import com.toxictrace.tanktracker.ui.theme.NeonOrange
import com.toxictrace.tanktracker.ui.theme.TankTrackerTheme
import com.toxictrace.tanktracker.viewmodel.PlayerViewModel
import com.toxictrace.tanktracker.viewmodel.UiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TankTrackerTheme {
                val vm: PlayerViewModel = viewModel()
                val uiState by vm.uiState.collectAsState()

                when (val state = uiState) {
                    is UiState.Success -> MainScreen(
                        profile = state.profile,
                        onSearchNewPlayer = { vm.resetToIdle() }
                    )
                    else -> SearchScreen(
                        uiState = state,
                        onSearch = { vm.searchPlayers(it) },
                        onSelectPlayer = { vm.loadProfile(it) }
                    )
                }
            }
        }
    }
}
