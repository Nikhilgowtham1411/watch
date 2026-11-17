package com.watchfacestudio.app.bluetooth.device

import com.watchfacestudio.app.bluetooth.model.DeviceIdentification
import com.watchfacestudio.app.bluetooth.model.DiscoveredDevice

class DeviceIdentifier {
    fun identifyDevice(device: DiscoveredDevice): DeviceIdentification? =
        DeviceDatabase.identify(device)

    fun identifyByName(name: String?): DeviceIdentification? = null
}
