package com.watchfacestudio.app.export

import android.content.Context
import com.watchfacestudio.app.data.local.db.entities.DesignEntity
import com.watchfacestudio.app.data.local.db.entities.DeviceEntity
import kotlinx.coroutines.delay
import java.io.File

sealed class InstallResult {
    data class Success(val message: String, val watchName: String) : InstallResult()
    data class OpenCompanion(val packageName: String?, val file: File) : InstallResult()
    data class Error(val reason: String) : InstallResult()
}

class WatchFaceInstaller(
    private val context: Context,
    private val exporter: WatchFaceExporter
) {
    suspend fun applyWatchFace(
        design: DesignEntity,
        device: DeviceEntity?,
        onProgress: (Float, String) -> Unit
    ): InstallResult {
        if (device == null) {
            return InstallResult.Error("No verified smartwatch is selected. Scan for nearby devices first.")
        }
        if (device.compatibilityStatus != "Verified profile") {
            return InstallResult.Error("This device has connected, but its watch-face format and installation method have not been verified.")
        }

        // A BLE connection alone does not implement a watch-face transfer protocol.
        // Never report success until a manufacturer-specific installer is implemented
        // and the target watch confirms the installation.
        return InstallResult.Error("Watch-face installation is not implemented for this device yet. The app will not report a false success.")
    }

}
