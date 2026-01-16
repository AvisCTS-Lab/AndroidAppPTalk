package com.avis.app.ptalk.domain.service

import com.avis.app.ptalk.core.websocket.ControlResponse
import com.avis.app.ptalk.core.websocket.DeviceControlWebSocket
import com.avis.app.ptalk.core.websocket.DeviceStatusResponse
import com.avis.app.ptalk.domain.control.WebSocketControlGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.thingai.base.log.ILog

/**
 * Service for managing real-time device control via WebSocket.
 * Provides a high-level API for the UI layer.
 */
class DeviceControlService(
    private val webSocket: DeviceControlWebSocket,
    private val serverUrl: String
) {
    private val TAG = "DeviceControlService"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val gateway = WebSocketControlGateway(webSocket, serverUrl)

    // Current device state
    private val _deviceStatus = MutableStateFlow<DeviceStatusResponse?>(null)
    val deviceStatus: StateFlow<DeviceStatusResponse?> = _deviceStatus.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    val connectionState = webSocket.connectionState

    private var currentDeviceId: String? = null

    /**
     * Connect to server and set target device
     */
    suspend fun connect(deviceId: String) {
        ILog.d(TAG, "connect() called with deviceId: $deviceId, serverUrl: $serverUrl")
        currentDeviceId = deviceId
        gateway.connect(deviceId)
        ILog.d(TAG, "gateway.connect() completed")
    }

    /**
     * Disconnect from server
     */
    fun disconnect() {
        webSocket.disconnect()
        currentDeviceId = null
        _deviceStatus.value = null
    }

    /**
     * Refresh device status
     */
    suspend fun refreshStatus() {
        ILog.d(TAG, "refreshStatus() called")
        _isLoading.value = true
        _lastError.value = null
        try {
            val status = gateway.requestStatus()
            ILog.d(TAG, "gateway.requestStatus() returned: $status")
            _deviceStatus.value = status
            if (status == null) {
                _lastError.value = "Failed to get device status"
            }
        } catch (e: Exception) {
            _lastError.value = e.message
            ILog.e(TAG, "refreshStatus failed", e.message)
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Set device volume (0-100)
     */
    suspend fun setVolume(volume: Int): Boolean {
        _isLoading.value = true
        _lastError.value = null
        return try {
            gateway.writeVolume(volume)
            // Update local state
            _deviceStatus.value = _deviceStatus.value?.copy(volume = volume)
            true
        } catch (e: Exception) {
            _lastError.value = e.message
            ILog.e(TAG, "setVolume failed", e.message)
            false
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Set device brightness (0-100)
     */
    suspend fun setBrightness(brightness: Int): Boolean {
        _isLoading.value = true
        _lastError.value = null
        return try {
            gateway.writeBrightness(brightness)
            // Update local state
            _deviceStatus.value = _deviceStatus.value?.copy(brightness = brightness)
            true
        } catch (e: Exception) {
            _lastError.value = e.message
            ILog.e(TAG, "setBrightness failed", e.message)
            false
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Set device name
     */
    suspend fun setDeviceName(name: String): Boolean {
        _isLoading.value = true
        _lastError.value = null
        return try {
            gateway.writeName(name)
            // Update local state
            _deviceStatus.value = _deviceStatus.value?.copy(deviceName = name)
            true
        } catch (e: Exception) {
            _lastError.value = e.message
            ILog.e(TAG, "setDeviceName failed", e.message)
            false
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Reboot device
     */
    suspend fun rebootDevice(): ControlResponse {
        _isLoading.value = true
        _lastError.value = null
        return try {
            val response = gateway.reboot()
            if (!response.isSuccess) {
                _lastError.value = response.message ?: "Reboot failed"
            }
            response
        } catch (e: Exception) {
            _lastError.value = e.message
            ILog.e(TAG, "rebootDevice failed", e.message)
            ControlResponse(status = "error", message = e.message)
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Request BLE config mode (factory reset WiFi)
     */
    suspend fun requestBleConfig(): ControlResponse {
        _isLoading.value = true
        _lastError.value = null
        return try {
            val response = gateway.requestBleConfig()
            if (!response.isSuccess) {
                _lastError.value = response.message ?: "BLE config request failed"
            }
            response
        } catch (e: Exception) {
            _lastError.value = e.message
            ILog.e(TAG, "requestBleConfig failed", e.message)
            ControlResponse(status = "error", message = e.message)
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Request OTA update
     */
    suspend fun requestOta(version: String? = null): ControlResponse {
        _isLoading.value = true
        _lastError.value = null
        return try {
            val response = gateway.requestOta(version)
            if (!response.isSuccess) {
                _lastError.value = response.message ?: "OTA request failed"
            }
            response
        } catch (e: Exception) {
            _lastError.value = e.message
            ILog.e(TAG, "requestOta failed", e.message)
            ControlResponse(status = "error", message = e.message)
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Get current battery level
     */
    suspend fun getBatteryLevel(): Int? {
        return try {
            gateway.getBatteryLevel()
        } catch (e: Exception) {
            ILog.e(TAG, "getBatteryLevel failed", e.message)
            null
        }
    }

    /**
     * Clear last error
     */
    fun clearError() {
        _lastError.value = null
    }
}