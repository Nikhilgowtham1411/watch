package com.watchfacestudio.app.bluetooth.device

import com.watchfacestudio.app.bluetooth.model.DeviceIdentification
import com.watchfacestudio.app.bluetooth.model.DiscoveredDevice

class DeviceIdentifier {
    fun identifyDevice(device: DiscoveredDevice): DeviceIdentification {
        return DeviceDatabase.identify(device.name)
    }

    fun identifyByName(name: String?): DeviceIdentification {
        return DeviceDatabase.identify(name)
    }
}
