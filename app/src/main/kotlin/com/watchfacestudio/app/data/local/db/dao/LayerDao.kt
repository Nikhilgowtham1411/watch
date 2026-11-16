package com.watchfacestudio.app.data.local.db.dao

import com.watchfacestudio.app.data.local.db.entities.LayerEntity
import kotlinx.coroutines.flow.Flow

interface LayerDao {
    suspend fun insert(layer: LayerEntity)
    suspend fun insertAll(layers: List<LayerEntity>)
    suspend fun update(layer: LayerEntity)
    suspend fun delete(layer: LayerEntity)
    suspend fun deleteById(id: String)
    suspend fun deleteByDesignId(designId: String)
    fun getByDesignId(designId: String): Flow<List<LayerEntity>>
    suspend fun getById(id: String): LayerEntity?
    suspend fun updateZIndex(id: String, zIndex: Int)
    suspend fun updateVisibility(id: String, visible: Boolean)
}
