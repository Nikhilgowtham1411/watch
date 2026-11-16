package com.watchfacestudio.app.data.local.db.entities

import com.watchfacestudio.app.data.model.ScreenShape

data class DeviceEntity(
    val id: String,
    val bluetoothAddress: String,
    val name: String,
    val brand: String,
    val model: String,
    val screenWidth: Int,
    val screenHeight: Int,
    val screenShape: ScreenShape,
    val compatibilityStatus: String,
    val installMethod: String,
    val lastConnectedAt: Long?,
    val isPrimary: Boolean
)
