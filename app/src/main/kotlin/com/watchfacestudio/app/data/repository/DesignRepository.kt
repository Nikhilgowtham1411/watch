package com.watchfacestudio.app.data.repository

import com.watchfacestudio.app.data.local.db.dao.ClockSettingsDao
import com.watchfacestudio.app.data.local.db.dao.DesignDao
import com.watchfacestudio.app.data.local.db.dao.LayerDao
import com.watchfacestudio.app.data.local.db.dao.WidgetSettingsDao
import com.watchfacestudio.app.data.local.db.entities.ClockSettingsEntity
import com.watchfacestudio.app.data.local.db.entities.DesignEntity
import com.watchfacestudio.app.data.local.db.entities.LayerEntity
import com.watchfacestudio.app.data.local.db.entities.WidgetSettingsEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class DesignRepository(
    private val designDao: DesignDao,
    private val layerDao: LayerDao,
    private val clockSettingsDao: ClockSettingsDao,
    private val widgetSettingsDao: WidgetSettingsDao
) {
    fun getAllDesigns(): Flow<List<DesignEntity>> = designDao.getAll()

    suspend fun getDesignById(id: String): DesignEntity? = designDao.getById(id)

    fun searchDesigns(query: String): Flow<List<DesignEntity>> = designDao.searchByName(query)

    suspend fun saveDesign(
        design: DesignEntity,
        layers: List<LayerEntity>,
        clockSettings: ClockSettingsEntity?,
        widgets: List<WidgetSettingsEntity>
    ) {
        designDao.insert(design)
        layerDao.deleteByDesignId(design.id)
        if (layers.isNotEmpty()) {
            layerDao.insertAll(layers)
        }
        if (clockSettings != null) {
            clockSettingsDao.insert(clockSettings)
        }
        widgetSettingsDao.deleteByDesignId(design.id)
        if (widgets.isNotEmpty()) {
            widgetSettingsDao.insertAll(widgets)
        }
    }

    suspend fun updateDesign(design: DesignEntity) {
        designDao.update(design)
    }

    suspend fun deleteDesign(id: String) {
        designDao.deleteById(id)
        layerDao.deleteByDesignId(id)
        clockSettingsDao.deleteById(id)
        widgetSettingsDao.deleteByDesignId(id)
    }

    suspend fun duplicateDesign(id: String): String? {
        val original = designDao.getById(id) ?: return null
        val newId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val newDesign = original.copy(
            id = newId,
            name = "${original.name} (Copy)",
            createdAt = now,
            modifiedAt = now
        )
        designDao.insert(newDesign)

        val originalClock = clockSettingsDao.getByDesignId(id)
        if (originalClock != null) {
            clockSettingsDao.insert(originalClock.copy(id = UUID.randomUUID().toString(), designId = newId))
        }

        return newId
    }

    fun getLayersForDesign(designId: String): Flow<List<LayerEntity>> = layerDao.getByDesignId(designId)

    suspend fun getClockSettings(designId: String): ClockSettingsEntity? = clockSettingsDao.getByDesignId(designId)

    fun getWidgetsForDesign(designId: String): Flow<List<WidgetSettingsEntity>> = widgetSettingsDao.getByDesignId(designId)
}
