package com.watchfacestudio.app.data.repository

import com.watchfacestudio.app.data.local.db.dao.DeviceDao
import com.watchfacestudio.app.data.local.db.entities.DeviceEntity
import com.watchfacestudio.app.data.local.preferences.AppPreferences
import kotlinx.coroutines.flow.Flow

class DeviceRepository(
    private val deviceDao: DeviceDao,
    private val appPreferences: AppPreferences
) {
    fun getPrimaryDevice(): Flow<DeviceEntity?> = deviceDao.getPrimaryDevice()

    fun getAllDevices(): Flow<List<DeviceEntity>> = deviceDao.getAllDevices()

    suspend fun getDeviceById(id: String): DeviceEntity? = deviceDao.getById(id)

    suspend fun saveDevice(device: DeviceEntity) {
        deviceDao.insert(device)
    }

    suspend fun setPrimaryDevice(device: DeviceEntity) {
        deviceDao.insert(device.copy(isPrimary = true, lastConnectedAt = System.currentTimeMillis()))
        appPreferences.setSelectedDeviceId(device.id)
    }

    suspend fun updateConnectionStatus(id: String, timestamp: Long?) {
        deviceDao.updateConnectionStatus(id, timestamp)
    }

    suspend fun removeDevice(id: String) {
        deviceDao.deleteById(id)
        appPreferences.setSelectedDeviceId(null)
    }
}
