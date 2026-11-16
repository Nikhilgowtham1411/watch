package com.watchfacestudio.app.ui.screens.imagecrop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

enum class CropAspectRatio(val label: String, val ratio: Float?) {
    FIT("Fit", null),
    RATIO_1_1("1:1", 1.0f),
    RATIO_4_5("4:5", 0.8f),
    RATIO_3_4("3:4", 0.75f),
    RATIO_9_16("9:16", 0.5625f)
}

class ImageCropViewModel(
    private val context: Context
) : ViewModel() {

    private val _selectedRatio = MutableStateFlow(CropAspectRatio.FIT)
    val selectedRatio: StateFlow<CropAspectRatio> = _selectedRatio.asStateFlow()

    private val _rotationDegrees = MutableStateFlow(0f)
    val rotationDegrees: StateFlow<Float> = _rotationDegrees.asStateFlow()

    private val _isFlippedHorizontally = MutableStateFlow(false)
    val isFlippedHorizontally: StateFlow<Boolean> = _isFlippedHorizontally.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    fun setAspectRatio(ratio: CropAspectRatio) {
        _selectedRatio.value = ratio
    }

    fun rotate90() {
        _rotationDegrees.value = (_rotationDegrees.value + 90f) % 360f
    }

    fun flipHorizontal() {
        _isFlippedHorizontally.value = !_isFlippedHorizontally.value
    }

    fun reset() {
        _selectedRatio.value = CropAspectRatio.FIT
        _rotationDegrees.value = 0f
        _isFlippedHorizontally.value = false
    }

    fun processAndSave(imageUri: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            _isProcessing.value = true
            val outputFile = withContext(Dispatchers.IO) {
                try {
                    val uri = Uri.parse(imageUri)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val originalBitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()

                    if (originalBitmap == null) return@withContext null

                    val matrix = Matrix()
                    if (_rotationDegrees.value != 0f) {
                        matrix.postRotate(_rotationDegrees.value)
                    }
                    if (_isFlippedHorizontally.value) {
                        matrix.postScale(-1f, 1f)
                    }

                    val transformed = Bitmap.createBitmap(
                        originalBitmap, 0, 0,
                        originalBitmap.width, originalBitmap.height,
                        matrix, true
                    )

                    val ratio = _selectedRatio.value.ratio
                    val cropped = if (ratio != null) {
                        val targetWidth: Int
                        val targetHeight: Int
                        if (transformed.width.toFloat() / transformed.height > ratio) {
                            targetHeight = transformed.height
                            targetWidth = (targetHeight * ratio).toInt()
                        } else {
                            targetWidth = transformed.width
                            targetHeight = (targetWidth / ratio).toInt()
                        }
                        val x = ((transformed.width - targetWidth) / 2).coerceAtLeast(0)
                        val y = ((transformed.height - targetHeight) / 2).coerceAtLeast(0)
                        Bitmap.createBitmap(transformed, x, y, targetWidth.coerceAtMost(transformed.width - x), targetHeight.coerceAtMost(transformed.height - y))
                    } else {
                        transformed
                    }

                    val cacheFile = File(context.cacheDir, "crop_${UUID.randomUUID()}.png")
                    val out = FileOutputStream(cacheFile)
                    cropped.compress(Bitmap.CompressFormat.PNG, 95, out)
                    out.flush()
                    out.close()

                    cacheFile.absolutePath
                } catch (e: Exception) {
                    null
                }
            }
            _isProcessing.value = false
            if (outputFile != null) {
                onComplete(outputFile)
            } else {
                onComplete(imageUri)
            }
        }
    }
}
