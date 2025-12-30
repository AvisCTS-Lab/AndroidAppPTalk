package com.avis.app.ptalk.core.ble.impl

import android.content.Context
import com.avis.app.ptalk.core.ble.BleClient
import com.avis.app.ptalk.core.ble.BleSession
import com.avis.app.ptalk.core.ble.ScannedDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import java.util.UUID

/**
 * Minimal stub to compile. Replace with a real GATT implementation.
 */
class PTalkBleClient(private val app: Context) : BleClient {
    override fun scanForConfigDevices(): Flow<List<ScannedDevice>> {
        // TODO: implement BluetoothLeScanner with a service UUID filter BleUuid.SVC_CONFIG
        return emptyFlow()
    }

    override suspend fun connect(address: String): BleSession {
        // TODO: create a BluetoothGatt connection, return a session wrapper
        return PTalkBleSession(address)
    }

    override suspend fun disconnect(address: String) {
        // TODO: track sessions and close by address
    }
}

private class PTalkBleSession(private val addr: String) : BleSession {
    private val _connected = MutableStateFlow(false)
    override val isConnected = _connected.asStateFlow()
    override val address: String get() = addr

    override suspend fun read(uuid: UUID): ByteArray {
        // TODO: read GATT characteristic
        return ByteArray(0)
    }

    override suspend fun write(uuid: UUID, value: ByteArray, withResponse: Boolean): ByteArray {
        // TODO: write GATT characteristic
        return value
    }

    override fun notifications(uuid: UUID): Flow<ByteArray> {
        // TODO: enable notifications for uuid
        return emptyFlow()
    }

    override suspend fun close() {
        // TODO: close GATT
    }
}