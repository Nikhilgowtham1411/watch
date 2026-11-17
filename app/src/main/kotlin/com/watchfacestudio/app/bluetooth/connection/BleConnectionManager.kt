package com.watchfacestudio.app.bluetooth.connection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.watchfacestudio.app.bluetooth.permission.BluetoothPermissionHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/** What the connected watch actually exposes over GATT. */
data class GattInfo(
    val address: String,
    val serviceUuids: List<UUID>,
    val batteryPercent: Int?
)

/**
 * Real GATT connection to a watch.
 *
 * [ConnectionState.Connected] is only published after the link is up and
 * service discovery has succeeded, so "Connected" always means the watch
 * answered. Callbacks run on the main thread.
 */
class BleConnectionManager(
    context: Context,
    private val bluetoothAdapter: BluetoothAdapter?,
    private val permissionHelper: BluetoothPermissionHelper
) {
    companion object {
        private val BATTERY_SERVICE: UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
        private val BATTERY_LEVEL: UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
        private const val CONNECT_TIMEOUT_MS = 20_000L
        private const val DISCOVERY_DELAY_MS = 600L
        private const val RETRY_DELAY_MS = 800L
    }

    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _gattInfo = MutableStateFlow<GattInfo?>(null)
    val gattInfo: StateFlow<GattInfo?> = _gattInfo.asStateFlow()

    private var currentGatt: BluetoothGatt? = null
    private var retriedOnce = false

    /** Incremented on every attempt/teardown so late callbacks from old sessions are ignored. */
    private var generation = 0

    private val timeoutRunnable = Runnable {
        if (_connectionState.value is ConnectionState.Connecting) {
            fail("Timed out. Keep the watch awake and close to the phone, and disconnect it from its own app.")
        }
    }

    private fun newCallback(gen: Int) = object : BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (gatt == null) return
            if (gen != generation) {
                gatt.close()
                return
            }
            if (newState == BluetoothProfile.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS) {
                // Give the link a moment to settle before service discovery;
                // discovering immediately fails on many watches.
                mainHandler.postDelayed({
                    if (gen == generation) gatt.discoverServices()
                }, DISCOVERY_DELAY_MS)
                return
            }

            val wasConnected = _connectionState.value is ConnectionState.Connected
            if (wasConnected) {
                closeGatt()
                _gattInfo.value = null
                _connectionState.value = ConnectionState.Disconnected
                return
            }

            val address = gatt.device.address
            if (status != BluetoothGatt.GATT_SUCCESS && !retriedOnce) {
                // First attempts often fail with status 133; one clean retry fixes most of them.
                retriedOnce = true
                closeGatt()
                mainHandler.postDelayed({
                    if (gen == generation) startGatt(address)
                }, RETRY_DELAY_MS)
                return
            }
            fail(describeStatus(status))
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (gatt == null || gen != generation) return
            if (status != BluetoothGatt.GATT_SUCCESS) {
                fail("Connected, but the watch did not list its services (status $status).")
                return
            }
            mainHandler.removeCallbacks(timeoutRunnable)
            val services = gatt.services.map { it.uuid }
            _gattInfo.value = GattInfo(gatt.device.address, services, batteryPercent = null)
            _connectionState.value = ConnectionState.Connected(gatt.device)

            val battery = gatt.getService(BATTERY_SERVICE)?.getCharacteristic(BATTERY_LEVEL)
            if (battery != null) {
                try {
                    gatt.readCharacteristic(battery)
                } catch (_: SecurityException) {
                }
            }
        }

        @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return
            handleRead(gen, characteristic, characteristic?.value, status)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            handleRead(gen, characteristic, value, status)
        }
    }

    private fun handleRead(
        gen: Int,
        characteristic: BluetoothGattCharacteristic?,
        value: ByteArray?,
        status: Int
    ) {
        if (gen != generation || characteristic?.uuid != BATTERY_LEVEL) return
        if (status != BluetoothGatt.GATT_SUCCESS || value == null || value.isEmpty()) return
        val percent = (value[0].toInt() and 0xFF).takeIf { it in 0..100 } ?: return
        _gattInfo.value = _gattInfo.value?.copy(batteryPercent = percent)
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String) {
        if (!permissionHelper.hasPermissions() || bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            _connectionState.value = ConnectionState.Failed("Bluetooth is off or permission is missing.")
            return
        }
        teardown()
        retriedOnce = false
        startGatt(address)
    }

    @SuppressLint("MissingPermission")
    private fun startGatt(address: String) {
        val adapter = bluetoothAdapter ?: return
        val gen = ++generation
        _gattInfo.value = null
        _connectionState.value = ConnectionState.Connecting

        val device = try {
            adapter.getRemoteDevice(address)
        } catch (e: IllegalArgumentException) {
            fail("Invalid Bluetooth address.")
            return
        }

        mainHandler.removeCallbacks(timeoutRunnable)
        mainHandler.postDelayed(timeoutRunnable, CONNECT_TIMEOUT_MS)
        try {
            currentGatt = device.connectGatt(
                appContext,
                false,
                newCallback(gen),
                BluetoothDevice.TRANSPORT_LE,
                BluetoothDevice.PHY_LE_1M_MASK,
                mainHandler
            )
            if (currentGatt == null) fail("Could not start a Bluetooth connection.")
        } catch (e: SecurityException) {
            fail("Bluetooth permission was denied.")
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        teardown()
        _gattInfo.value = null
        _connectionState.value = ConnectionState.Disconnected
    }

    @SuppressLint("MissingPermission")
    private fun closeGatt() {
        currentGatt?.let {
            try {
                it.disconnect()
                it.close()
            } catch (_: SecurityException) {
            }
        }
        currentGatt = null
    }

    private fun teardown() {
        generation++
        mainHandler.removeCallbacks(timeoutRunnable)
        closeGatt()
    }

    private fun fail(reason: String) {
        teardown()
        _gattInfo.value = null
        _connectionState.value = ConnectionState.Failed(reason)
    }

    private fun describeStatus(status: Int): String = when (status) {
        133 -> "Watch not reachable (error 133). It may be connected to its own app — disconnect it there and try again."
        8 -> "Connection timed out. Move the watch closer."
        19, 22 -> "The watch closed the connection."
        else -> "Connection failed (status $status)."
    }
}
