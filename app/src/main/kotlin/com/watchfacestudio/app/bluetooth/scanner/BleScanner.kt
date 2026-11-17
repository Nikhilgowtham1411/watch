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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Scans for BLE devices that are physically advertising right now.
 *
 * Every entry in [discoveredDevices] comes from a real advertisement. Entries
 * that stop advertising (watch moved away, turned off, or connected to its own
 * app) are dropped after [STALE_AFTER_MS], so the list only shows what is
 * actually nearby.
 */
class BleScanner(
    private val bluetoothAdapter: BluetoothAdapter?,
    private val scanResultMapper: ScanResultMapper,
    private val permissionHelper: BluetoothPermissionHelper
) {
    companion object {
        const val STALE_AFTER_MS = 10_000L
        private const val PRUNE_INTERVAL_MS = 2_000L
    }

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<DiscoveredDevice>> = _discoveredDevices.asStateFlow()

    private var timeoutJob: Job? = null
    private var pruneJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private fun upsert(results: List<DiscoveredDevice>) {
        if (results.isEmpty()) return
        _discoveredDevices.update { current ->
            val merged = LinkedHashMap<String, DiscoveredDevice>()
            current.forEach { merged[it.address] = it }
            for (incoming in results) {
                val previous = merged[incoming.address]
                merged[incoming.address] = if (previous == null) {
                    incoming
                } else {
                    // Advertisement and scan-response packets arrive separately;
                    // keep a name or service list seen earlier instead of losing it.
                    incoming.copy(
                        name = incoming.name?.takeIf { it.isNotBlank() } ?: previous.name,
                        serviceUuids = (previous.serviceUuids + incoming.serviceUuids).distinct()
                    )
                }
            }
            merged.values.toList()
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result ?: return
            upsert(listOf(scanResultMapper.map(result)))
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results ?: return
            upsert(results.map { scanResultMapper.map(it) })
        }

        override fun onScanFailed(errorCode: Int) {
            timeoutJob?.cancel()
            pruneJob?.cancel()
            _isScanning.value = false
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan(timeoutMillis: Long = 120_000L) {
        if (!permissionHelper.hasPermissions()) return
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) return
        val scanner: BluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner ?: return

        if (_isScanning.value) return

        _discoveredDevices.value = emptyList()
        _isScanning.value = true

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        try {
            scanner.startScan(null, settings, scanCallback)
        } catch (e: Exception) {
            _isScanning.value = false
            return
        }

        pruneJob?.cancel()
        pruneJob = scope.launch {
            while (true) {
                delay(PRUNE_INTERVAL_MS)
                val cutoff = System.currentTimeMillis() - STALE_AFTER_MS
                _discoveredDevices.update { list -> list.filter { it.lastSeenMs >= cutoff } }
            }
        }

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
        pruneJob?.cancel()
        pruneJob = null

        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled && permissionHelper.hasPermissions()) {
            try {
                bluetoothAdapter.bluetoothLeScanner?.stopScan(scanCallback)
            } catch (_: Exception) {
            }
        }
        _isScanning.value = false
    }
}
