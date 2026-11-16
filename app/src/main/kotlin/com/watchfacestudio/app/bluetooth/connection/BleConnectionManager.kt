package com.watchfacestudio.app.bluetooth.connection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.watchfacestudio.app.bluetooth.permission.BluetoothPermissionHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BleConnectionManager(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter?,
    private val permissionHelper: BluetoothPermissionHelper
) {
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var currentGatt: BluetoothGatt? = null

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    if (gatt != null) {
                        _connectionState.value = ConnectionState.Connected(gatt.device)
                        gatt.discoverServices()
                    }
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.value = ConnectionState.Disconnected
                    gatt?.close()
                    currentGatt = null
                }
                else -> {
                    if (status != BluetoothGatt.GATT_SUCCESS) {
                        _connectionState.value = ConnectionState.Failed("Connection error: $status")
                        gatt?.close()
                        currentGatt = null
                    }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            // Services discovered
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String) {
        if (!permissionHelper.hasPermissions() || bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            _connectionState.value = ConnectionState.Failed("Bluetooth or permissions unavailable")
            return
        }

        disconnect()
        _connectionState.value = ConnectionState.Connecting

        val device = bluetoothAdapter.getRemoteDevice(address)
        currentGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (currentGatt != null) {
            _connectionState.value = ConnectionState.Disconnecting
            currentGatt?.disconnect()
            currentGatt?.close()
            currentGatt = null
        }
        _connectionState.value = ConnectionState.Disconnected
    }
}
