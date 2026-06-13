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
        val current = _uiState.value
        val newBlocked = settingsManager.blockedCount
        val newTrackers = settingsManager.trackersCount
        val newData = settingsManager.dataSaved.toDouble()
        val newRecent = settingsManager.recentBlocked.split(",").filter { it.isNotEmpty() }

        if (current.blockedCount != newBlocked || 
            current.trackersCount != newTrackers || 
            current.dataSavedMb != newData || 
            current.recentBlocked != newRecent) {
            
            _uiState.value = current.copy(
                blockedCount = newBlocked,
                trackersCount = newTrackers,
                dataSavedMb = newData,
                recentBlocked = newRecent
            )
        }
    }
}
