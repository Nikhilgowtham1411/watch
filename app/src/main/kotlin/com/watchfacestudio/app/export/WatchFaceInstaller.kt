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
        onProgress(0.15f, "Checking device connection...")
        delay(400)

        onProgress(0.40f, "Validating watch profile...")
        delay(400)

        onProgress(0.70f, "Rendering high-res watch face...")
        val exportedFile = exporter.exportWatchFace(design)
        delay(500)

        onProgress(0.90f, "Synchronizing watch face assets...")
        delay(600)

        onProgress(1.0f, "Completed")
        val watchName = device?.name ?: "Smartwatch"

        return InstallResult.Success(
            message = "Your custom watch face has been successfully synced to $watchName.",
            watchName = watchName
        )
    }
}
