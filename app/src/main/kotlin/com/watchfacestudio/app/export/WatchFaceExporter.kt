package com.watchfacestudio.app.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import com.watchfacestudio.app.data.local.db.entities.DesignEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class WatchFaceExporter(
    private val context: Context
) {
    suspend fun exportWatchFace(
        design: DesignEntity,
        timeText: String = "10:08",
        dateText: String = "Mon, Nov 16",
        targetWidth: Int? = null,
        targetHeight: Int? = null
    ): File = withContext(Dispatchers.IO) {
        val width = (targetWidth?.takeIf { it > 0 } ?: design.canvasWidth).coerceIn(240, 2048)
        val height = (targetHeight?.takeIf { it > 0 } ?: design.canvasHeight).coerceIn(240, 2048)

        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        if (!design.imagePath.isNullOrBlank()) {
            try {
                val inputStream = if (design.imagePath.startsWith("content://") || design.imagePath.startsWith("file://")) {
                    context.contentResolver.openInputStream(Uri.parse(design.imagePath))
                } else {
                    File(design.imagePath).inputStream()
                }
                val bgBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bgBitmap != null) {
                    // Center-crop artwork to preserve proportions on different watch displays.
                    val sourceAspect = bgBitmap.width.toFloat() / bgBitmap.height.toFloat()
                    val targetAspect = width.toFloat() / height.toFloat()
                    val srcRect = if (sourceAspect > targetAspect) {
                        val cropWidth = (bgBitmap.height * targetAspect).toInt().coerceAtLeast(1)
                        val left = (bgBitmap.width - cropWidth) / 2
                        Rect(left, 0, left + cropWidth, bgBitmap.height)
                    } else {
                        val cropHeight = (bgBitmap.width / targetAspect).toInt().coerceAtLeast(1)
                        val top = (bgBitmap.height - cropHeight) / 2
                        Rect(0, top, bgBitmap.width, top + cropHeight)
                    }
                    canvas.drawBitmap(bgBitmap, srcRect, Rect(0, 0, width, height), null)
                    bgBitmap.recycle()
                }
            } catch (e: Exception) {
                canvas.drawColor(android.graphics.Color.DKGRAY)
            }
        } else {
            canvas.drawColor(android.graphics.Color.parseColor("#14141E"))
        }

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = height * 0.16f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            setShadowLayer(8f, 0f, 4f, android.graphics.Color.BLACK)
        }
        val textX = width / 2f
        val textY = height * 0.50f
        canvas.drawText(timeText, textX, textY, textPaint)

        val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#A78BFA")
            textSize = height * 0.05f
            textAlign = Paint.Align.CENTER
            setShadowLayer(4f, 0f, 2f, android.graphics.Color.BLACK)
        }
        canvas.drawText(dateText, textX, textY + (height * 0.07f), datePaint)

        val exportFile = File(context.cacheDir, "export_${UUID.randomUUID()}.png")
        val out = FileOutputStream(exportFile)
        outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()

        exportFile
    }
}
