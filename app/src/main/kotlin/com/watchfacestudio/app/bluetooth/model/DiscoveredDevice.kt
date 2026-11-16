package com.watchfacestudio.app.bluetooth.model

import android.os.ParcelUuid

data class DiscoveredDevice(
    val name: String?,
    val address: String,
    val rssi: Int,
    val serviceUuids: List<ParcelUuid>,
    val manufacturerData: Map<Int, ByteArray>,
    val bondState: Int,
    val identified: Boolean,
    val deviceInfo: DeviceIdentification?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DiscoveredDevice) return false
        return address == other.address
    }

    override fun hashCode(): Int = address.hashCode()
}
