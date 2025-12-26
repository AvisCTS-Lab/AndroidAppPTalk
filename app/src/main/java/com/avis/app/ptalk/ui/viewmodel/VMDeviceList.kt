package com.avis.app.ptalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class DeviceConnectionStatus {
    ONLINE,
    OFFLINE,
    SLEEP
}

data class DeviceState (
    val name: String,
    val status: DeviceConnectionStatus,
    val lastSeenMinutes: Int
)

val MockDeviceList = listOf (
    DeviceState("Device 1", DeviceConnectionStatus.ONLINE, 23),
    DeviceState("Device 2", DeviceConnectionStatus.OFFLINE, 23),
    DeviceState("Device 3", DeviceConnectionStatus.SLEEP, 23),
    DeviceState("Device 4", DeviceConnectionStatus.ONLINE, 23),
    DeviceState("Device 5", DeviceConnectionStatus.ONLINE, 23),
    DeviceState("Device 6", DeviceConnectionStatus.ONLINE, 23),
)

data class DeviceListUiState (
    val devices: List<DeviceState> = emptyList(),
    val isLoading: Boolean = false,
    val isListExpanded: Boolean = false,
)

class VMDeviceList : ViewModel() {
    private val _uiState = MutableStateFlow(DeviceListUiState())
    val uiState: StateFlow<DeviceListUiState> = _uiState.asStateFlow()

    fun loadDeviceList() {
        _uiState.value = _uiState.value.copy(devices = MockDeviceList, isLoading = false)
    }

    fun getDeviceCount(): Int {
       return _uiState.value.devices.size
    }

    fun toggleListExpanded() {
        _uiState.value = _uiState.value.copy(isListExpanded = !_uiState.value.isListExpanded)
    }
}