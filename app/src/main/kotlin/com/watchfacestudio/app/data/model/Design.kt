package com.watchfacestudio.app.data.model

data class Design(
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
