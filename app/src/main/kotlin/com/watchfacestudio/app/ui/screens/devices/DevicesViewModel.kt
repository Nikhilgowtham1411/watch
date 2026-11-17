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
import com.watchfacestudio.app.bluetooth.scanner.WatchClassifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
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

    private val _showAllDevices = MutableStateFlow(true)
    val showAllDevices: StateFlow<Boolean> = _showAllDevices.asStateFlow()

    /** Nearby devices, watches only unless the user asks to see everything. Strongest signal first. */
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
            connectionManager.connect(device.address)
            val result = withTimeoutOrNull(20_000L) {
                connectionManager.connectionState.first { state ->
                    state is ConnectionState.Connected || state is ConnectionState.Failed
                }
            }
            // Only persist a verified watch profile after the BLE GATT connection succeeds.
            val connected = result as? ConnectionState.Connected ?: return@launch
            if (connected.device.address != device.address) return@launch
            val profile = device.deviceInfo

            // Keep the selected BLE device across navigation, even when its
            // watch-face protocol is unknown. Unknown never means compatible.
            val entity = DeviceEntity(
                id = device.address,
                bluetoothAddress = device.address,
                name = device.name?.takeIf { it.isNotBlank() } ?: "Unknown BLE device",
                brand = profile?.brand ?: "Unknown",
                model = profile?.model ?: "BLE device",
                screenWidth = profile?.screenWidth ?: 0,
                screenHeight = profile?.screenHeight ?: 0,
                screenShape = profile?.screenShape ?: ScreenShape.RECTANGULAR,
                compatibilityStatus = if (profile?.supportsCustomWatchFace == true) "Verified profile" else "Not verified",
                installMethod = profile?.installMethod?.name ?: "UNSUPPORTED",
                lastConnectedAt = System.currentTimeMillis(),
                isPrimary = true
            )
            deviceRepository.setPrimaryDevice(entity)
        }
    }

    fun selectSavedDevice(device: DeviceEntity) {
        viewModelScope.launch {
            deviceRepository.setPrimaryDevice(device)
        }
    }
}
