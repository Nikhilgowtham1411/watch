package com.watchfacestudio.app.data.local.db.dao

import com.watchfacestudio.app.data.local.db.entities.DeviceEntity
import kotlinx.coroutines.flow.Flow

interface DeviceDao {
    suspend fun insert(device: DeviceEntity)
    suspend fun update(device: DeviceEntity)
    suspend fun delete(device: DeviceEntity)
    suspend fun deleteById(id: String)
    suspend fun getById(id: String): DeviceEntity?
    fun getPrimaryDevice(): Flow<DeviceEntity?>
    fun getAllDevices(): Flow<List<DeviceEntity>>
    suspend fun updateConnectionStatus(id: String, timestamp: Long?)
}
