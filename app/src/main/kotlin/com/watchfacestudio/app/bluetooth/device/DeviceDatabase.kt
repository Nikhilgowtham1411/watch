package com.watchfacestudio.app.bluetooth.device

import com.watchfacestudio.app.bluetooth.model.DeviceIdentification
import com.watchfacestudio.app.bluetooth.model.DiscoveredDevice

/**
 * No guessed watch profiles are returned from a Bluetooth advertisement.
 *
 * A BLE local name is not proof of a watch model, screen resolution or
 * watch-face installation protocol. Add a profile only after its model and
 * integration have been verified against the manufacturer's documentation.
 */
object DeviceDatabase {
    private val verifiedProfiles: List<Pair<(DiscoveredDevice) -> Boolean, DeviceIdentification>> =
        emptyList()

    fun identify(device: DiscoveredDevice): DeviceIdentification? {
        return verifiedProfiles.firstOrNull { (matches, _) -> matches(device) }?.second
    }

    fun verifiedProfileCount(): Int = verifiedProfiles.size
}
