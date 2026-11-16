package com.watchfacestudio.app.bluetooth.model

import com.watchfacestudio.app.data.model.ScreenShape

enum class ConfidenceLevel {
    HIGH,
    MEDIUM,
    LOW
}

enum class InstallMethod {
    COMPANION_APP,
    BLE_TRANSFER,
    WEAR_OS,
    MANUAL_EXPORT,
    UNSUPPORTED
}

data class DeviceIdentification(
    val brand: String,
    val model: String,
    val screenWidth: Int,
    val screenHeight: Int,
    val screenShape: ScreenShape,
    val supportsCustomWatchFace: Boolean,
    val installMethod: InstallMethod,
    val companionAppPackage: String?,
    val confidence: ConfidenceLevel
)
