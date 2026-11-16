package com.watchfacestudio.app.data.local.db.entities

import com.watchfacestudio.app.data.model.ScreenShape

data class DesignEntity(
    val id: String,
    val name: String,
    val imagePath: String?,
    val optimizedImagePath: String?,
    val canvasWidth: Int,
    val canvasHeight: Int,
    val canvasShape: ScreenShape,
    val targetDeviceId: String?,
    val createdAt: Long,
    val modifiedAt: Long
)
