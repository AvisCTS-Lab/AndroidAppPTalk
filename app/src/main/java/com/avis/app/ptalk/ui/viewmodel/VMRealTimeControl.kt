package com.avis.app.ptalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avis.app.ptalk.core.websocket.DeviceControlWebSocket
import com.avis.app.ptalk.core.websocket.DeviceStatusResponse
import com.avis.app.ptalk.domain.control.ControlGateway
import com.avis.app.ptalk.domain.service.DeviceControlService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VMRealTimeControl @Inject constructor(
    private val deviceControlService: DeviceControlService
) : ViewModel() {

    data class RealTimeUiState(
        val volume: Float = 0.5f,
        val brightness: Float = 0.5f,
        val showScreen: Boolean = true,
        val lockDevice: Boolean = false,
        val deviceName: String = "",
        val batteryLevel: Int? = null,
        val firmwareVersion: String = "",
        val wifiSsid: String = "",
        val wifiRssi: Int? = null,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val successMessage: String? = null
    )

    private val _uiState = MutableStateFlow(RealTimeUiState())
    val uiState: StateFlow<RealTimeUiState> = _uiState.asStateFlow()

    private val _deviceId = MutableStateFlow("")
    val deviceId: StateFlow<String> = _deviceId.asStateFlow()

    val isConnected: StateFlow<Boolean> = deviceControlService.connectionState
        .map { it == DeviceControlWebSocket.ConnectionState.CONNECTED }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * Initialize control for a device
     */
    fun initDevice(deviceId: String) {
        android.util.Log.d("VMRealTimeControl", "initDevice called with deviceId: $deviceId")
        _deviceId.value = deviceId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                android.util.Log.d("VMRealTimeControl", "Calling deviceControlService.connect()")
                deviceControlService.connect(deviceId)
                android.util.Log.d(
                    "VMRealTimeControl",
                    "connect() completed, calling refreshDeviceStatus()"
                )
                refreshDeviceStatus()
            } catch (e: Exception) {
                android.util.Log.e("VMRealTimeControl", "initDevice error: ${e.message}")
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }

    /**
     * Refresh device status from server
     */
    fun refreshDeviceStatus() {
        viewModelScope.launch {
            android.util.Log.d("VMRealTimeControl", "refreshDeviceStatus() called")
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                android.util.Log.d(
                    "VMRealTimeControl",
                    "Calling deviceControlService.refreshStatus()"
                )
                deviceControlService.refreshStatus()
                val status = deviceControlService.deviceStatus.value
                android.util.Log.d("VMRealTimeControl", "refreshStatus completed, status=$status")
                if (status != null) {
                    android.util.Log.d(
                        "VMRealTimeControl",
                        "Status received: volume=${status.volume}, brightness=${status.brightness}, battery=${status.batteryLevel}"
                    )
                    _uiState.update { current ->
                        current.copy(
                            volume = (status.volume ?: 50) / 100f,
                            brightness = (status.brightness ?: 50) / 100f,
                            deviceName = status.deviceName ?: "",
                            batteryLevel = status.batteryLevel,
                            firmwareVersion = status.firmwareVersion ?: "",
                            wifiSsid = status.wifiSsid ?: "",
                            wifiRssi = status.wifiRssi,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    android.util.Log.e("VMRealTimeControl", "Status is null")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Không thể lấy trạng thái thiết bị"
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("VMRealTimeControl", "refreshDeviceStatus error: ${e.message}")
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    /**
     * Set volume (0.0 - 1.0)
     */
    fun setVolume(volume: Float) {
        _uiState.update { it.copy(volume = volume) }
    }

    /**
     * Apply volume change to device
     */
    fun applyVolume() {
        viewModelScope.launch {
            val volumeInt = (_uiState.value.volume * 100).toInt()
            _uiState.update { it.copy(isLoading = true) }
            val success = deviceControlService.setVolume(volumeInt)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    successMessage = if (success) "Đã cập nhật âm lượng" else null,
                    errorMessage = if (!success) "Lỗi cập nhật âm lượng" else null
                )
            }
        }
    }

    /**
     * Set brightness (0.0 - 1.0)
     */
    fun setBrightness(brightness: Float) {
        _uiState.update { it.copy(brightness = brightness) }
    }

    /**
     * Apply brightness change to device
     */
    fun applyBrightness() {
        viewModelScope.launch {
            val brightnessInt = (_uiState.value.brightness * 100).toInt()
            _uiState.update { it.copy(isLoading = true) }
            val success = deviceControlService.setBrightness(brightnessInt)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    successMessage = if (success) "Đã cập nhật độ sáng" else null,
                    errorMessage = if (!success) "Lỗi cập nhật độ sáng" else null
                )
            }
        }
    }

    /**
     * Set device name
     */
    fun setDeviceName(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = deviceControlService.setDeviceName(name)
            if (success) {
                _uiState.update {
                    it.copy(
                        deviceName = name,
                        isLoading = false,
                        successMessage = "Đã đổi tên thiết bị"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Lỗi đổi tên thiết bị")
                }
            }
        }
    }

    fun toggleShowScreen() {
        _uiState.update { it.copy(showScreen = !it.showScreen) }
        // TODO: Send command when firmware supports it
    }

    fun toggleLockDevice() {
        _uiState.update { it.copy(lockDevice = !it.lockDevice) }
        // TODO: Send command when firmware supports it
    }

    /**
     * Restart device
     */
    fun restartDevice() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val response = deviceControlService.rebootDevice()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    successMessage = if (response.isSuccess) "Thiết bị đang khởi động lại..." else null,
                    errorMessage = if (!response.isSuccess) response.message else null
                )
            }
        }
    }

    /**
     * Factory reset - enter BLE config mode
     */
    fun factoryReset() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val response = deviceControlService.requestBleConfig()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    successMessage = if (response.isSuccess) "Thiết bị đang chuyển sang chế độ cấu hình BLE" else null,
                    errorMessage = if (!response.isSuccess) response.message else null
                )
            }
        }
    }

    /**
     * Request OTA update
     */
    fun requestOtaUpdate() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val response = deviceControlService.requestOta()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    successMessage = if (response.isSuccess) "Đang kiểm tra cập nhật firmware..." else null,
                    errorMessage = if (!response.isSuccess) response.message else null
                )
            }
        }
    }

    /**
     * Clear messages
     */
    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        deviceControlService.disconnect()
    }

    // Legacy method for compatibility
    fun getDeviceControlState(deviceId: String) {
        initDevice(deviceId)
    }
}