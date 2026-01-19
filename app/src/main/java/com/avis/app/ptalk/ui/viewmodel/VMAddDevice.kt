package com.avis.app.ptalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avis.app.ptalk.core.ble.BleClient
import com.avis.app.ptalk.core.ble.ScannedDevice
import com.avis.app.ptalk.domain.control.ControlGateway
import com.avis.app.ptalk.domain.control.WifiNetwork
import com.avis.app.ptalk.domain.data.local.repo.DeviceRepository
import com.avis.app.ptalk.domain.model.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.thingai.base.log.ILog
import javax.inject.Inject

@HiltViewModel
class VMAddDevice @Inject constructor(
    private val ble: BleClient,
    private val deviceControlGateway: ControlGateway,
    private val deviceRepository: DeviceRepository
) : ViewModel() {
    private val TAG = "VMAddDevice"

    data class UiState(
        val scanning: Boolean = false,
        val devices: List<ScannedDevice> = emptyList(),
        val error: String? = null,
        val wifiNetworks: List<WifiNetwork> = emptyList(),
        val loadingWifiList: Boolean = false,
        val deviceId: String = ""
    )

    private val _ui = MutableStateFlow(UiState())
    val ui = _ui.asStateFlow()

    private var scanJob: Job? = null

    private var deviceAddress: String? = null

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

    fun connectDevice(deviceAddress: String, onConnected: () -> Unit) {
        this.deviceAddress = deviceAddress
        viewModelScope.launch {
            deviceControlGateway.connect(deviceAddress)
            deviceControlGateway.isConnected.collect { connected ->
                ILog.d(TAG, "connectDevice", "$connected")
                if (connected) {
                    // Load WiFi list and device ID after connection - wait for completion
                    loadWifiListAndDeviceIdAsync()
                    onConnected()
                    this.cancel()
                }
            }
        }
    }

    /**
     * Load WiFi list and device ID synchronously (blocking until complete)
     */
    private suspend fun loadWifiListAndDeviceIdAsync() {
        try {
            _ui.value = _ui.value.copy(loadingWifiList = true)

            // Read device ID
            val deviceId = try {
                deviceControlGateway.readDeviceId()
            } catch (e: Exception) {
                ILog.e(TAG, "readDeviceId failed", e.message)
                ""
            }

            // Read WiFi list
            val wifiList = try {
                deviceControlGateway.readWifiList()
            } catch (e: Exception) {
                ILog.e(TAG, "readWifiList failed", e.message)
                emptyList()
            }

            _ui.value = _ui.value.copy(
                wifiNetworks = wifiList,
                deviceId = deviceId,
                loadingWifiList = false
            )
            ILog.d(
                TAG,
                "loadWifiListAndDeviceIdAsync",
                "deviceId=$deviceId, networks=${wifiList.size}"
            )
        } catch (e: Exception) {
            _ui.value = _ui.value.copy(loadingWifiList = false, error = e.message)
            ILog.e(TAG, "loadWifiListAndDeviceIdAsync", e.message)
        }
    }

    fun refreshWifiList() {
        viewModelScope.launch {
            loadWifiListAndDeviceIdAsync()
        }
    }

    fun disconnectDevice() {
        viewModelScope.launch {
            deviceControlGateway.disconnect()
            deviceAddress = null
        }
    }

    fun connectWifi(ssid: String, pass: String, onConnected: () -> Unit) {
        viewModelScope.launch {
            try {
                deviceControlGateway.writeWifiSsid(ssid)
                deviceControlGateway.writeWifiPass(pass)
                deviceControlGateway.saveConfig()
                onConnected()
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(error = e.message)
                ILog.e(TAG, "connectWifi", e.message)
            }
        }
    }

    fun configDeviceOnConnect(
        ssid: String, pass: String,
        volume: Float, brightness: Float, callback: DeviceConfigCallback
    ) {
        viewModelScope.launch {
            try {
                deviceControlGateway.writeWifiSsid(ssid)
                deviceControlGateway.writeWifiPass(pass)
                deviceControlGateway.writeVolume((volume * 100).toInt())
                deviceControlGateway.writeBrightness((brightness * 100).toInt())

                val device = Device("PTalk Device", deviceAddress!!)
                device.deviceId = _ui.value.deviceId  // Save deviceId from BLE
                device.appVersion = deviceControlGateway.readAppVersion()
                device.buildInfo = deviceControlGateway.readBuildInfo()
                deviceRepository.upsert(device)

                ILog.d(TAG, "configDeviceOnConnect", "Saving config with ssid=$ssid")

                deviceControlGateway.saveConfig()

                callback.onConfigSaved()
            } catch (e: Exception) {
                ILog.d(TAG, "configDeviceOnConnect", e.message)
                callback.onConfigError(e.message ?: "")
            }
        }
    }

    interface DeviceConnectionCallback {
        fun onDeviceConnected(deviceAddress: String)
        fun onDeviceDisconnected(deviceAddress: String)
    }

    interface DeviceConfigCallback {
        fun onConfigSaved()
        fun onConfigError(error: String)
    }
}