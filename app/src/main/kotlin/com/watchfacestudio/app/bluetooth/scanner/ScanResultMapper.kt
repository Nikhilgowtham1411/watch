package com.watchfacestudio.app.bluetooth.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import com.watchfacestudio.app.bluetooth.device.DeviceIdentifier
import com.watchfacestudio.app.bluetooth.model.DiscoveredDevice

class ScanResultMapper(
    private val deviceIdentifier: DeviceIdentifier
) {
    @SuppressLint("MissingPermission")
    fun map(scanResult: ScanResult): DiscoveredDevice {
        val device: BluetoothDevice = scanResult.device
        val scanRecord = scanResult.scanRecord
        val name = scanRecord?.deviceName ?: device.name

        val serviceUuids = scanRecord?.serviceUuids ?: emptyList()
        val manufacturerData = mutableMapOf<Int, ByteArray>()
        val rawManData = scanRecord?.manufacturerSpecificData
        if (rawManData != null) {
            for (i in 0 until rawManData.size()) {
                val key = rawManData.keyAt(i)
                val value = rawManData.valueAt(i)
                manufacturerData[key] = value
            }
        }

        val partialDevice = DiscoveredDevice(
            name = name,
            address = device.address,
            rssi = scanResult.rssi,
            serviceUuids = serviceUuids,
            manufacturerData = manufacturerData,
            bondState = device.bondState,
            identified = false,
            deviceInfo = null
        )

        val info = deviceIdentifier.identifyDevice(partialDevice)
        return partialDevice.copy(
            identified = info != null,
            deviceInfo = info
        )
    }
}
