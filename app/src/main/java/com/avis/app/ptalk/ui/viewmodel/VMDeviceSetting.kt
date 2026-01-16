package com.avis.app.ptalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avis.app.ptalk.core.websocket.DeviceControlWebSocket
import com.avis.app.ptalk.domain.data.local.repo.DeviceRepository
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
import org.thingai.base.log.ILog
import javax.inject.Inject

@HiltViewModel
class VMDeviceSetting @Inject constructor(
    private val deviceControlService: DeviceControlService,
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    private val TAG = "VMDeviceSetting"

    data class DeviceSettingUiState(
        val isLoading: Boolean = false,
        val isConnected: Boolean = false,
        val message: String? = null,
        val shouldNavigateBack: Boolean = false
    )

    private val _uiState = MutableStateFlow(DeviceSettingUiState())
    val uiState: StateFlow<DeviceSettingUiState> = _uiState.asStateFlow()

    private var currentDeviceId: String? = null
    private var currentMacAddress: String? = null

    init {
        // Observe connection state
        viewModelScope.launch {
            deviceControlService.connectionState.collect { state ->
                _uiState.update {
                    it.copy(isConnected = state == DeviceControlWebSocket.ConnectionState.CONNECTED)
                }
            }
        }
    }

    fun initDevice(deviceId: String, macAddress: String) {
        currentDeviceId = deviceId
        currentMacAddress = macAddress
        viewModelScope.launch {
            try {
                deviceControlService.connect(deviceId)
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    /**
     * Unlink device: send factory reset command and delete from local database
     */
    fun unlinkDevice() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 1. Send factory reset (BLE config) command to device
                ILog.d(TAG, "Sending factory reset command...")
                val response = deviceControlService.requestBleConfig()
                if (!response.isSuccess) {
                    ILog.w(
                        TAG,
                        "Factory reset failed: ${response.message}, but still deleting from DB"
                    )
                }

                // 2. Delete device from local database
                currentMacAddress?.let { mac ->
                    ILog.d(TAG, "Deleting device from DB: $mac")
                    deviceRepository.delete(mac)
                }

                // 3. Disconnect WebSocket
                deviceControlService.disconnect()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message = "\u0110\u00e3 h\u1ee7y li\u00ean k\u1ebft thi\u1ebft b\u1ecb",
                        shouldNavigateBack = true
                    )
                }
            } catch (e: Exception) {
                ILog.e(TAG, "unlinkDevice error", e.message)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        message = "L\u1ed7i: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearNavigateBack() {
        _uiState.update { it.copy(shouldNavigateBack = false) }
    }

    fun rebootDevice() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val response = deviceControlService.rebootDevice()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    message = if (response.isSuccess) "Thiết bị đang khởi động lại..."
                    else "Lỗi: ${response.message}"
                )
            }
        }
    }

    fun factoryReset() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val response = deviceControlService.requestBleConfig()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    message = if (response.isSuccess) "Thiết bị đang khôi phục cài đặt gốc..."
                    else "Lỗi: ${response.message}"
                )
            }
        }
    }

    fun requestOta() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val response = deviceControlService.requestOta()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    message = if (response.isSuccess) "Đang kiểm tra cập nhật firmware..."
                    else "Lỗi: ${response.message}"
                )
            }
        }
    }

    fun requestBleConfig() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val response = deviceControlService.requestBleConfig()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    message = if (response.isSuccess) "Thiết bị đang chuyển sang chế độ Bluetooth..."
                    else "Lỗi: ${response.message}"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    override fun onCleared() {
        super.onCleared()
        // Don't disconnect here as the service may be shared
    }
}