package com.example.adnt

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import com.example.adnt.data.SettingsManager
import com.example.adnt.ui.screens.DashboardScreen
import com.example.adnt.ui.screens.SettingsScreen
import com.example.adnt.ui.screens.AppExclusionScreen
import com.example.adnt.ui.theme.AdntTheme
import com.example.adnt.ui.viewmodels.VpnViewModel
import com.example.adnt.vpn.AdntVpnService
import android.provider.Settings

class MainActivity : ComponentActivity() {
    private val viewModel: VpnViewModel by viewModels()
    private lateinit var settingsManager: SettingsManager

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startVpnService()
            viewModel.toggleVpn()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(this)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.uiState.collectAsState()
            var currentScreen by remember { mutableStateOf("dashboard") }
            
            // Periodically refresh stats
            LaunchedEffect(Unit) {
                while (true) {
                    try {
                        viewModel.refreshStats(settingsManager)
                    } catch (e: Exception) {
                        // Ignore refresh errors
                    }
                    delay(1000) // Refresh every 1 second for better responsiveness
                }
            }

            AdntTheme {
                if (currentScreen == "dashboard") {
                    DashboardScreen(
                        state = state,
                        onToggle = { 
                            if (state.isEnabled) {
                                stopVpnService()
                            } else {
                                prepareVpn()
                            }
                        },
                        onOpenAccessibilitySettings = {
                            startActivity(Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        },
                        onOpenSettings = {
                            currentScreen = "settings"
                        }
                    )
                } else if (currentScreen == "settings") {
                    SettingsScreen(
                        settingsManager = settingsManager,
                        onBack = { currentScreen = "dashboard" },
                        onAppExclusion = { currentScreen = "exclusion" }
                    )
                } else {
                    AppExclusionScreen(
                        settingsManager = settingsManager,
                        onBack = { currentScreen = "settings" }
                    )
                }
            }
        }
    }

    private fun prepareVpn() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            vpnPermissionLauncher.launch(intent)
        } else {
            startVpnService()
            if (!viewModel.uiState.value.isEnabled) {
                viewModel.toggleVpn()
            }
        }
    }

    private fun startVpnService() {
        val intent = Intent(this, AdntVpnService::class.java).apply {
            action = AdntVpnService.ACTION_START
        }
        startForegroundService(intent)
    }

    private fun stopVpnService() {
        val intent = Intent(this, AdntVpnService::class.java).apply {
            action = AdntVpnService.ACTION_STOP
        }
        startService(intent)
        viewModel.toggleVpn()
    }
}
