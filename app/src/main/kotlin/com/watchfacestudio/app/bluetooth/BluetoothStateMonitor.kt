package com.watchfacestudio.app.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.watchfacestudio.app.bluetooth.model.BluetoothState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothStateMonitor(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter?
) {
    private val _bluetoothState = MutableStateFlow(getCurrentState())
    val bluetoothState: StateFlow<BluetoothState> = _bluetoothState.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                _bluetoothState.value = when (state) {
                    BluetoothAdapter.STATE_ON -> BluetoothState.ENABLED
                    BluetoothAdapter.STATE_OFF -> BluetoothState.DISABLED
                    BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.TURNING_ON
                    BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.TURNING_OFF
                    else -> BluetoothState.NOT_AVAILABLE
                }
            }
        }
    }

    init {
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(receiver, filter)
    }

    private fun getCurrentState(): BluetoothState {
        if (bluetoothAdapter == null) return BluetoothState.NOT_AVAILABLE
        return if (bluetoothAdapter.isEnabled) BluetoothState.ENABLED else BluetoothState.DISABLED
    }
}
