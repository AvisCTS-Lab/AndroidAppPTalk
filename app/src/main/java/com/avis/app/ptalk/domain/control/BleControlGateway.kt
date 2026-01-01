package com.avis.app.ptalk.domain.control

import com.avis.app.ptalk.core.ble.BleSession
import com.avis.app.ptalk.core.config.BleUuid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.thingai.base.log.ILog
import java.util.UUID

/**
 * BLE-backed implementation of ControlGateway.
 *
 * - Volume and Brightness are single-byte [0..100].
 * - Name, WiFi SSID/PASS, AppVersion, BuildInfo are UTF-8 strings.
 * - Save command writes UTF-8 strings
 */
class BleControlGateway(
    private val connector: suspend (String) -> BleSession
) : ControlGateway {
    private val TAG = "BleControlGateway"

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var session: BleSession? = null
    private var connectionJob: Job? = null

    private val _connected = MutableStateFlow(false)
    override val isConnected: Flow<Boolean> = _connected.asStateFlow()

    override suspend fun connect(address: String) {
        // Establish session
        session = connector(address)

        // Re-collect connection state
        try {
            connectionJob?.cancel()
            connectionJob = scope.launch {
                requireSession().isConnected.collect { ok ->
                    ILog.d(TAG, "connect $address", "$ok")
                    _connected.value = ok
                }
            }
        } catch (e: Exception) {
            ILog.e(TAG, "connect failed", e.message)
            disconnect()
        }
    }

    override suspend fun disconnect() {
        ILog.d(TAG, "disconnect")
        connectionJob?.cancel()
        connectionJob = null
        session?.close()
        session = null
        _connected.value = false
    }

    // Device name
    override suspend fun readName(): String =
        readString(BleUuid.CHR_DEVICE_NAME)

    override suspend fun writeName(value: String) {
        writeString(BleUuid.CHR_DEVICE_NAME, value)
    }

    // Volume [0..100]
    override suspend fun readVolume(): Int =
        readBytePercent(BleUuid.CHR_VOLUME)

    override suspend fun writeVolume(value: Int) {
        writeBytePercent(BleUuid.CHR_VOLUME, value)
    }

    // Brightness [0..100]
    override suspend fun readBrightness(): Int =
        readBytePercent(BleUuid.CHR_BRIGHTNESS)

    override suspend fun writeBrightness(value: Int) {
        writeBytePercent(BleUuid.CHR_BRIGHTNESS, value)
    }

    // WiFi
    override suspend fun readWifiSsid(): String =
        readString(BleUuid.CHR_WIFI_SSID)

    override suspend fun writeWifiSsid(value: String) {
        writeString(BleUuid.CHR_WIFI_SSID, value)
    }

    override suspend fun writeWifiPass(value: String) {
        writeString(BleUuid.CHR_WIFI_PASS, value)
    }

    // Metadata (read-only)
    override suspend fun readAppVersion(): String =
        readString(BleUuid.CHR_APP_VERSION)

    override suspend fun readBuildInfo(): String =
        readString(BleUuid.CHR_BUILD_INFO)

    // Persist current config on device
    override suspend fun saveConfig() {
        writeByte(BleUuid.CHR_SAVE_CMD, 0x01)
    }

    // ---- Helpers ----

    private fun requireSession(): BleSession =
        session ?: error("BLE session is not connected")

    private suspend fun readString(uuid: UUID): String {
        val bytes = requireSession().read(uuid)
        return bytes.decodeToString()
    }

    private suspend fun writeString(uuid: UUID, value: String) {
        requireSession().write(uuid, value.toByteArray(Charsets.UTF_8))
    }

    private suspend fun readByte(uuid: UUID): Int {
        val bytes = requireSession().read(uuid)
        return (bytes.firstOrNull() ?: 0).toInt() and 0xFF
    }

    private suspend fun writeByte(uuid: UUID, value: Int) {
        val v = value and 0xFF
        requireSession().write(uuid, byteArrayOf(v.toByte()))
    }

    private suspend fun readBytePercent(uuid: UUID): Int =
        readByte(uuid).coerceIn(0, 100)

    private suspend fun writeBytePercent(uuid: UUID, value: Int) {
        writeByte(uuid, value.coerceIn(0, 100))
    }
}