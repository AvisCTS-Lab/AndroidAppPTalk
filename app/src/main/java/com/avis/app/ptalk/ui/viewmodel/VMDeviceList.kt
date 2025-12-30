package com.avis.app.ptalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.avis.app.ptalk.domain.data.local.repo.DeviceRepository
import com.avis.app.ptalk.domain.define.DeviceConnectionStatus
import com.avis.app.ptalk.domain.model.DeviceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class VMDeviceList @Inject constructor(
    private val deviceRepo: DeviceRepository
) : ViewModel() {
    data class DeviceListUiState (
        val devices: List<DeviceState> = emptyList(),
        val isLoading: Boolean = false,
        val isListExpanded: Boolean = false,
    )

    private val _uiState = MutableStateFlow(DeviceListUiState())
    val uiState: StateFlow<DeviceListUiState> = _uiState.asStateFlow()

    fun loadDeviceList() {
        val MockDeviceList = listOf (
            DeviceState("Tên thiết bị", DeviceConnectionStatus.ONLINE, 23),
            DeviceState("Tên thiết bị", DeviceConnectionStatus.OFFLINE, 23),
            DeviceState("Tên thiết bị", DeviceConnectionStatus.SLEEP, 23),
            DeviceState("Tên thiết bị", DeviceConnectionStatus.ONLINE, 23),
            DeviceState("Tên thiết bị", DeviceConnectionStatus.ONLINE, 23),
            DeviceState("Tên thiết bị", DeviceConnectionStatus.ONLINE, 23),
        )

        _uiState.value = _uiState.value.copy(devices = MockDeviceList, isLoading = false)
    }

    fun getDeviceCount(): Int {
       return _uiState.value.devices.size
    }

    fun toggleListExpanded() {
        _uiState.value = _uiState.value.copy(isListExpanded = !_uiState.value.isListExpanded)
    }
}