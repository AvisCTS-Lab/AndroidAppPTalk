package com.avis.app.ptalk.domain.control

import com.avis.app.ptalk.core.ble.BleSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BleControlGateway(private val connector: suspend (String) -> BleSession) : ControlGateway {
    private var session: BleSession? = null
    private val _connected = MutableStateFlow(false)
    override val isConnected: Flow<Boolean> = _connected.asStateFlow()

    override suspend fun connect(address: String) {
        session = connector(address)
        session!!.isConnected.collect {
            _connected.value = it
        }
    }

    override suspend fun disconnect() {
        TODO("Not yet implemented")
    }

    override suspend fun readName(): String {
        TODO("Not yet implemented")
    }

    override suspend fun writeName(value: String) {
        TODO("Not yet implemented")
    }

    override suspend fun readVolume(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun writeVolume(value: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun readBrightness(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun writeBrightness(value: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun readWifiSsid(): String {
        TODO("Not yet implemented")
    }

    override suspend fun writeWifiSsid(value: String) {
        TODO("Not yet implemented")
    }

    override suspend fun writeWifiPass(value: String) {
        TODO("Not yet implemented")
    }

    override suspend fun readAppVersion(): String {
        TODO("Not yet implemented")
    }

    override suspend fun readBuildInfo(): String {
        TODO("Not yet implemented")
    }

    override suspend fun saveConfig() {
        TODO("Not yet implemented")
    }
}