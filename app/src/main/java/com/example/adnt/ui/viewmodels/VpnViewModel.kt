package com.example.adnt.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.adnt.data.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class VpnState(
    val isEnabled: Boolean = false,
    val blockedCount: Int = 0,
    val trackersCount: Int = 0,
    val dataSavedMb: Double = 0.0,
    val recentBlocked: List<String> = emptyList()
)

class VpnViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VpnState())
    val uiState: StateFlow<VpnState> = _uiState.asStateFlow()

    fun toggleVpn() {
        _uiState.value = _uiState.value.copy(isEnabled = !_uiState.value.isEnabled)
    }

    fun refreshStats(settingsManager: SettingsManager) {
        _uiState.value = _uiState.value.copy(
            blockedCount = settingsManager.blockedCount,
            trackersCount = settingsManager.trackersCount,
            dataSavedMb = settingsManager.dataSaved.toDouble(),
            recentBlocked = settingsManager.recentBlocked.split(",").filter { it.isNotEmpty() }
        )
    }
}
