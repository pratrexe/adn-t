package com.example.adnt.ui.viewmodels

import androidx.lifecycle.ViewModel
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

    // Mock function to simulate blocking for UI demo
    fun simulateBlock(domain: String) {
        val current = _uiState.value
        _uiState.value = current.copy(
            blockedCount = current.blockedCount + 1,
            recentBlocked = (listOf(domain) + current.recentBlocked).take(10)
        )
    }
}
