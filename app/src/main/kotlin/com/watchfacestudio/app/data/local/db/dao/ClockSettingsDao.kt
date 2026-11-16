package com.watchfacestudio.app.data.local.db.dao

import com.watchfacestudio.app.data.local.db.entities.ClockSettingsEntity

interface ClockSettingsDao {
    suspend fun insert(clockSettings: ClockSettingsEntity)
    suspend fun update(clockSettings: ClockSettingsEntity)
    suspend fun delete(clockSettings: ClockSettingsEntity)
    suspend fun deleteById(id: String)
    suspend fun getByDesignId(designId: String): ClockSettingsEntity?
    suspend fun getById(id: String): ClockSettingsEntity?
}
