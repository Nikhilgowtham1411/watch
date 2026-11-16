package com.watchfacestudio.app.data.model

data class WatchDevice(
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
    val isPrimary: Boolean,
    val capabilities: List<DeviceCapability> = emptyList()
)
