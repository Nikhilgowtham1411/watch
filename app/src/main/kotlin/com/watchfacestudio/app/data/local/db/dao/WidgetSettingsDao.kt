package com.watchfacestudio.app.data.local.db.dao

import com.watchfacestudio.app.data.local.db.entities.WidgetSettingsEntity
import kotlinx.coroutines.flow.Flow

interface WidgetSettingsDao {
    suspend fun insert(widgetSettings: WidgetSettingsEntity)
    suspend fun insertAll(widgetSettings: List<WidgetSettingsEntity>)
    suspend fun update(widgetSettings: WidgetSettingsEntity)
    suspend fun delete(widgetSettings: WidgetSettingsEntity)
    suspend fun deleteById(id: String)
    suspend fun deleteByDesignId(designId: String)
    fun getByDesignId(designId: String): Flow<List<WidgetSettingsEntity>>
    suspend fun getById(id: String): WidgetSettingsEntity?
}
