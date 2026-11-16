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
        dateText: String = "Mon, Nov 16"
    ): File = withContext(Dispatchers.IO) {
        val width = design.canvasWidth.coerceAtLeast(240)
        val height = design.canvasHeight.coerceAtLeast(240)

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
                    val srcRect = Rect(0, 0, bgBitmap.width, bgBitmap.height)
                    val dstRect = Rect(0, 0, width, height)
                    canvas.drawBitmap(bgBitmap, srcRect, dstRect, null)
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
