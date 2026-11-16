package com.watchfacestudio.app.bluetooth.connection

import android.bluetooth.BluetoothDevice

sealed class ConnectionState {
    data object Disconnected : ConnectionState()
    data object Connecting : ConnectionState()
    data class Connected(val device: BluetoothDevice) : ConnectionState()
    data object Disconnecting : ConnectionState()
    data class Failed(val reason: String) : ConnectionState()
}
