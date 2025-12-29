package com.avis.app.ptalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VMRealTimeControl : ViewModel() {
    data class RealTimeUiState(
        val volume: Float = 0.5f,
        val showScreen: Boolean = true,
        val lockDevice: Boolean = false
    )

    private val _uiState = MutableStateFlow(RealTimeUiState())
    val uiState: StateFlow<RealTimeUiState> = _uiState.asStateFlow()

    private val _deviceId = MutableStateFlow("")
    val deviceId: StateFlow<String> = _deviceId.asStateFlow()

    fun getDeviceControlState(deviceId: String) {
        _deviceId.value = deviceId
        _uiState.update { it.copy(lockDevice = true, showScreen = false, volume = 0.5f) }
    }

    fun setVolume(volume: Float) {
        _uiState.update { it.copy(volume = volume) }
    }

    fun toggleShowScreen() {
        _uiState.update { it.copy(showScreen = !it.showScreen) }
    }

    fun toggleLockDevice() {
        _uiState.update { it.copy(lockDevice = !it.lockDevice) }
    }

    fun restartDevice() {
        // TODO: UI-only for now
    }
}