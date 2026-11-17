package com.watchfacestudio.app.ui.screens.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchfacestudio.app.bluetooth.BluetoothStateMonitor
import com.watchfacestudio.app.bluetooth.connection.BleConnectionManager
import com.watchfacestudio.app.bluetooth.connection.ConnectionState
import com.watchfacestudio.app.bluetooth.connection.GattInfo
import com.watchfacestudio.app.bluetooth.model.BluetoothState
import com.watchfacestudio.app.bluetooth.model.DiscoveredDevice
import com.watchfacestudio.app.bluetooth.permission.BluetoothPermissionHelper
import com.watchfacestudio.app.bluetooth.scanner.BleScanner
import com.watchfacestudio.app.bluetooth.scanner.WatchClassifier
import com.watchfacestudio.app.data.local.db.entities.DeviceEntity
import com.watchfacestudio.app.data.model.ScreenShape
import com.watchfacestudio.app.data.repository.DeviceRepository
import com.watchfacestudio.app.export.WatchCompanionApps
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class DevicesViewModel(
    private val bleScanner: BleScanner,
    private val connectionManager: BleConnectionManager,
    private val permissionHelper: BluetoothPermissionHelper,
    private val bluetoothStateMonitor: BluetoothStateMonitor,
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    val isScanning: StateFlow<Boolean> = bleScanner.isScanning
    val discoveredDevices: StateFlow<List<DiscoveredDevice>> = bleScanner.discoveredDevices

    /** Watches only by default; the switch on the screen reveals every nearby BLE device. */
    private val _showAllDevices = MutableStateFlow(false)
    val showAllDevices: StateFlow<Boolean> = _showAllDevices.asStateFlow()

    /** Devices advertising right now, watches only unless asked otherwise, strongest signal first. */
    val visibleDevices: StateFlow<List<DiscoveredDevice>> = combine(
        bleScanner.discoveredDevices, _showAllDevices
    ) { devices, showAll ->
        devices
            .filter { WatchClassifier.isNearby(it) }
            .filter { showAll || WatchClassifier.isLikelyWatch(it) }
            .sortedByDescending { it.rssi }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setShowAllDevices(show: Boolean) { _showAllDevices.value = show }

    val connectionState: StateFlow<ConnectionState> = connectionManager.connectionState
    val gattInfo: StateFlow<GattInfo?> = connectionManager.gattInfo
    val bluetoothState: StateFlow<BluetoothState> = bluetoothStateMonitor.bluetoothState

    private val _connectingAddress = MutableStateFlow<String?>(null)
    val connectingAddress: StateFlow<String?> = _connectingAddress.asStateFlow()

    val primaryDevice: StateFlow<DeviceEntity?> = deviceRepository.getPrimaryDevice().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun hasPermissions(): Boolean = permissionHelper.hasPermissions()

    fun getRequiredPermissions(): List<String> = permissionHelper.getRequiredPermissions()

    fun isLocationOff(): Boolean = permissionHelper.isLocationOffForScanning()

    fun startScanning() {
        bleScanner.startScan()
    }

    fun stopScanning() {
        bleScanner.stopScan()
    }

    fun disconnect() {
        connectionManager.disconnect()
    }

    fun connectToDevice(device: DiscoveredDevice) {
        if (_connectingAddress.value != null) return
        viewModelScope.launch {
            _connectingAddress.value = device.address
            try {
                // Scanning and connecting at the same time makes many phones fail with error 133.
                bleScanner.stopScan()
                connectionManager.connect(device.address)
                val result = withTimeoutOrNull(25_000L) {
                    connectionManager.connectionState.first { state ->
                        state is ConnectionState.Connected || state is ConnectionState.Failed
                    }
                }
                // Only a watch that answered over GATT is saved as the selected device.
                val connected = result as? ConnectionState.Connected ?: return@launch
                if (connected.device.address != device.address) return@launch

                val services = (connectionManager.gattInfo.value?.serviceUuids ?: emptyList()) +
                    device.serviceUuids.map { it.uuid }
                val companion = WatchCompanionApps.candidate(device.name, services)

                val entity = DeviceEntity(
                    id = device.address,
                    bluetoothAddress = device.address,
                    name = device.name?.takeIf { it.isNotBlank() } ?: "Unnamed watch",
                    brand = device.deviceInfo?.brand ?: "Unknown",
                    model = device.deviceInfo?.model ?: "BLE watch",
                    // Screen size cannot be read from a generic BLE watch; 0 means "use the design's canvas".
                    screenWidth = device.deviceInfo?.screenWidth ?: 0,
                    screenHeight = device.deviceInfo?.screenHeight ?: 0,
                    screenShape = device.deviceInfo?.screenShape ?: ScreenShape.RECTANGULAR,
                    compatibilityStatus = "Connected",
                    installMethod = WatchCompanionApps.toInstallMethod(companion),
                    lastConnectedAt = System.currentTimeMillis(),
                    isPrimary = true
                )
                deviceRepository.setPrimaryDevice(entity)
            } finally {
                _connectingAddress.value = null
            }
        }
    }

    fun selectSavedDevice(device: DeviceEntity) {
        viewModelScope.launch {
            deviceRepository.setPrimaryDevice(device)
        }
    }
}
