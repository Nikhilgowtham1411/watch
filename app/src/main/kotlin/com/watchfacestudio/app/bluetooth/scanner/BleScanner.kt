package com.watchfacestudio.app.bluetooth.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import com.watchfacestudio.app.bluetooth.model.DiscoveredDevice
import com.watchfacestudio.app.bluetooth.permission.BluetoothPermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BleScanner(
    private val bluetoothAdapter: BluetoothAdapter?,
    private val scanResultMapper: ScanResultMapper,
    private val permissionHelper: BluetoothPermissionHelper
) {
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<DiscoveredDevice>> = _discoveredDevices.asStateFlow()

    private var timeoutJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result ?: return
            val mapped = scanResultMapper.map(result)
            val currentList = _discoveredDevices.value.toMutableList()
            val existingIndex = currentList.indexOfFirst { it.address == mapped.address }
            if (existingIndex >= 0) {
                currentList[existingIndex] = mapped
            } else {
                currentList.add(mapped)
            }
            _discoveredDevices.value = currentList
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results ?: return
            val currentList = _discoveredDevices.value.toMutableList()
            for (result in results) {
                val mapped = scanResultMapper.map(result)
                val existingIndex = currentList.indexOfFirst { it.address == mapped.address }
                if (existingIndex >= 0) {
                    currentList[existingIndex] = mapped
                } else {
                    currentList.add(mapped)
                }
            }
            _discoveredDevices.value = currentList
        }

        override fun onScanFailed(errorCode: Int) {
            _isScanning.value = false
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan(timeoutMillis: Long = 15000L) {
        if (!permissionHelper.hasPermissions()) return
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) return
        val scanner: BluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner ?: return

        if (_isScanning.value) return

        _discoveredDevices.value = emptyList()
        _isScanning.value = true

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner.startScan(null, settings, scanCallback)

        timeoutJob?.cancel()
        timeoutJob = scope.launch {
            delay(timeoutMillis)
            stopScan()
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (!_isScanning.value) return
        timeoutJob?.cancel()
        timeoutJob = null

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled && permissionHelper.hasPermissions()) {
            val scanner = bluetoothAdapter.bluetoothLeScanner
            scanner?.stopScan(scanCallback)
        }
        _isScanning.value = false
    }
}
