package com.avis.app.ptalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avis.app.ptalk.domain.data.local.repo.DeviceRepository
import com.avis.app.ptalk.domain.model.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMDeviceList @Inject constructor(
    private val deviceRepository: DeviceRepository
) : ViewModel() {
    data class DeviceListUiState (
        val devices: List<Device> = emptyList(),
        val isLoading: Boolean = false,
        val isListExpanded: Boolean = false,
    )

    private val _uiState = MutableStateFlow(DeviceListUiState())
    val uiState: StateFlow<DeviceListUiState> = _uiState.asStateFlow()

    fun loadDeviceList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            deviceRepository.devices().collect { devices ->
                _uiState.value = _uiState.value.copy(devices = devices, isLoading = false)
            }
        }
    }

    fun getDeviceCount(): Int {
       return _uiState.value.devices.size
    }

    fun toggleListExpanded() {
        _uiState.value = _uiState.value.copy(isListExpanded = !_uiState.value.isListExpanded)
    }
}