package com.watchfacestudio.app

import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.Context
import com.watchfacestudio.app.bluetooth.BluetoothStateMonitor
import com.watchfacestudio.app.bluetooth.connection.BleConnectionManager
import com.watchfacestudio.app.bluetooth.device.DeviceIdentifier
import com.watchfacestudio.app.bluetooth.permission.BluetoothPermissionHelper
import com.watchfacestudio.app.bluetooth.scanner.BleScanner
import com.watchfacestudio.app.bluetooth.scanner.ScanResultMapper
import com.watchfacestudio.app.data.local.db.DatabaseHelper
import com.watchfacestudio.app.data.local.preferences.AppPreferences
import com.watchfacestudio.app.data.repository.DesignRepository
import com.watchfacestudio.app.data.repository.DeviceRepository
import com.watchfacestudio.app.export.WatchFaceExporter
import com.watchfacestudio.app.export.WatchFaceInstaller

class AppContainer(context: Context) {
    val preferences = AppPreferences(context)
    val dbHelper = DatabaseHelper(context)

    val designRepository = DesignRepository(
        designDao = dbHelper.designDao,
        layerDao = dbHelper.layerDao,
        clockSettingsDao = dbHelper.clockSettingsDao,
        widgetSettingsDao = dbHelper.widgetSettingsDao
    )

    val deviceRepository = DeviceRepository(
        deviceDao = dbHelper.deviceDao,
        appPreferences = preferences
    )

    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    val bluetoothAdapter = bluetoothManager?.adapter
    val permissionHelper = BluetoothPermissionHelper(context)
    val deviceIdentifier = DeviceIdentifier()
    val scanResultMapper = ScanResultMapper(deviceIdentifier)

    val bleScanner = BleScanner(
        bluetoothAdapter = bluetoothAdapter,
        scanResultMapper = scanResultMapper,
        permissionHelper = permissionHelper
    )

    val bleConnectionManager = BleConnectionManager(
        context = context,
        bluetoothAdapter = bluetoothAdapter,
        permissionHelper = permissionHelper
    )

    val bluetoothStateMonitor = BluetoothStateMonitor(
        context = context,
        bluetoothAdapter = bluetoothAdapter
    )

    val watchFaceExporter = WatchFaceExporter(context)

    val watchFaceInstaller = WatchFaceInstaller(
        context = context,
        exporter = watchFaceExporter
    )
}

class WatchFaceStudioApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = AppContainer(this)
    }

    companion object {
        lateinit var instance: WatchFaceStudioApp
            private set
    }
}
