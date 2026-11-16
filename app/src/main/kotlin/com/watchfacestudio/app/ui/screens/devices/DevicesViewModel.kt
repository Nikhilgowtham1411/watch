package com.watchfacestudio.app.ui.screens.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchfacestudio.app.bluetooth.BluetoothStateMonitor
import com.watchfacestudio.app.bluetooth.connection.BleConnectionManager
import com.watchfacestudio.app.bluetooth.connection.ConnectionState
import com.watchfacestudio.app.bluetooth.model.BluetoothState
import com.watchfacestudio.app.bluetooth.model.DiscoveredDevice
import com.watchfacestudio.app.bluetooth.permission.BluetoothPermissionHelper
import com.watchfacestudio.app.bluetooth.scanner.BleScanner
import com.watchfacestudio.app.data.local.db.entities.DeviceEntity
import com.watchfacestudio.app.data.model.ScreenShape
import com.watchfacestudio.app.data.repository.DeviceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DevicesViewModel(
    private val bleScanner: BleScanner,
    private val connectionManager: BleConnectionManager,
    private val permissionHelper: BluetoothPermissionHelper,
    private val bluetoothStateMonitor: BluetoothStateMonitor,
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    val isScanning: StateFlow<Boolean> = bleScanner.isScanning
    val discoveredDevices: StateFlow<List<DiscoveredDevice>> = bleScanner.discoveredDevices
    val connectionState: StateFlow<ConnectionState> = connectionManager.connectionState
    val bluetoothState: StateFlow<BluetoothState> = bluetoothStateMonitor.bluetoothState

    val primaryDevice: StateFlow<DeviceEntity?> = deviceRepository.getPrimaryDevice().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun hasPermissions(): Boolean = permissionHelper.hasPermissions()

    fun getRequiredPermissions(): List<String> = permissionHelper.getRequiredPermissions()

    fun startScanning() {
        bleScanner.startScan()
    }

    fun stopScanning() {
        bleScanner.stopScan()
    }

    fun connectToDevice(device: DiscoveredDevice) {
        viewModelScope.launch {
            val entity = DeviceEntity(
                id = device.address,
                bluetoothAddress = device.address,
                name = device.name ?: device.deviceInfo?.model ?: "Smartwatch",
                brand = device.deviceInfo?.brand ?: "Generic",
                model = device.deviceInfo?.model ?: "Smartwatch",
                screenWidth = device.deviceInfo?.screenWidth ?: 360,
                screenHeight = device.deviceInfo?.screenHeight ?: 360,
                screenShape = device.deviceInfo?.screenShape ?: ScreenShape.CIRCULAR,
                compatibilityStatus = "Compatible",
                installMethod = device.deviceInfo?.installMethod?.name ?: "COMPANION_APP",
                lastConnectedAt = System.currentTimeMillis(),
                isPrimary = true
            )
            deviceRepository.setPrimaryDevice(entity)
            connectionManager.connect(device.address)
        }
    }

    fun selectSavedDevice(device: DeviceEntity) {
        viewModelScope.launch {
            deviceRepository.setPrimaryDevice(device)
        }
    }
}
