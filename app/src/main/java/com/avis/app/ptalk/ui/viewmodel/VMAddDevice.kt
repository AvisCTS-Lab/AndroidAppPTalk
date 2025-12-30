package com.avis.app.ptalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avis.app.ptalk.core.ble.BleClient
import com.avis.app.ptalk.core.ble.ScannedDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMAddDevice @Inject constructor(
    private val ble: BleClient
) : ViewModel() {

    data class UiState(
        val scanning: Boolean = false,
        val devices: List<ScannedDevice> = emptyList(),
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui = _ui.asStateFlow()

    private var scanJob: Job? = null

    fun startScan() {
        // Cancel any existing scan subscription
        scanJob?.cancel()
        _ui.value = _ui.value.copy(scanning = true, error = null, devices = emptyList())

        scanJob = ble.scanForConfigDevices()
            .onEach { list -> _ui.value = _ui.value.copy(devices = list) }
            .catch { e -> _ui.value = _ui.value.copy(error = e.message, scanning = false) }
            .let { flow ->
                viewModelScope.launch { flow.collect { /* handled in onEach above */ } }
            }
    }

    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
        _ui.value = _ui.value.copy(scanning = false)
    }

    fun selectDevice(address: String) {
        // For demo, just stop scan; you can navigate to control screen with the address
        stopScan()
    }
}