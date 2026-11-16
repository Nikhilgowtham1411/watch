package com.watchfacestudio.app.data.local.db.dao

import com.watchfacestudio.app.data.local.db.entities.DesignEntity
import kotlinx.coroutines.flow.Flow

interface DesignDao {
    suspend fun insert(design: DesignEntity)
    suspend fun update(design: DesignEntity)
    suspend fun delete(design: DesignEntity)
    suspend fun deleteById(id: String)
    fun getAll(): Flow<List<DesignEntity>>
    suspend fun getById(id: String): DesignEntity?
    fun searchByName(query: String): Flow<List<DesignEntity>>
}
