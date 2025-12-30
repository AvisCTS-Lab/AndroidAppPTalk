package com.avis.app.ptalk.domain.data.local.repo

import com.avis.app.ptalk.domain.data.local.dao.DeviceDao
import com.avis.app.ptalk.domain.model.Device
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeviceRepository @Inject constructor (private val dao: DeviceDao) {
    fun devices(): Flow<List<Device>> = dao.getAllDevice()
    suspend fun upsert(device: Device) = dao.upsert(device)
    suspend fun get(address: String) = dao.get(address)
    suspend fun delete(address: String) = dao.deleteByAddress(address)
}