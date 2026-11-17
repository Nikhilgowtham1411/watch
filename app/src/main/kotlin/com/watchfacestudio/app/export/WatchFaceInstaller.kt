package com.watchfacestudio.app.export

import android.content.Context
import android.net.Uri
import com.watchfacestudio.app.data.local.db.entities.DesignEntity
import com.watchfacestudio.app.data.local.db.entities.DeviceEntity
import java.io.File

sealed class InstallResult {
    /** Reserved for a verified direct BLE installer that the watch itself confirmed. */
    data class Success(val message: String, val watchName: String) : InstallResult()

    /** Face rendered at the watch's size and saved; the watch's own app does the final transfer. */
    data class ReadyToSend(
        val imageUri: Uri,
        val file: File,
        val width: Int,
        val height: Int,
        val companion: WatchCompanionApps.Companion?,
        val watchName: String
    ) : InstallResult()

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
            return InstallResult.Error("No watch selected. Open Devices and connect to your watch first.")
        }

        val width = device.screenWidth.takeIf { it > 0 } ?: design.canvasWidth
        val height = device.screenHeight.takeIf { it > 0 } ?: design.canvasHeight

        onProgress(0.25f, "Rendering watch face (${width} × ${height})...")
        val file = try {
            exporter.exportWatchFace(
                design,
                targetWidth = device.screenWidth.takeIf { it > 0 },
                targetHeight = device.screenHeight.takeIf { it > 0 }
            )
        } catch (e: Exception) {
            return InstallResult.Error("Could not render the watch face: ${e.message}")
        }

        onProgress(0.6f, "Saving image to your gallery...")
        val uri = WatchFaceShare.saveToGallery(context, file)
            ?: return InstallResult.Error("Could not save the image to the gallery.")

        onProgress(0.85f, "Looking for your watch app...")
        val companion = WatchCompanionApps.resolveInstalled(context, device.name, device.installMethod)

        onProgress(1f, "Ready")
        return InstallResult.ReadyToSend(
            imageUri = uri,
            file = file,
            width = width,
            height = height,
            companion = companion,
            watchName = device.name
        )
    }
}
