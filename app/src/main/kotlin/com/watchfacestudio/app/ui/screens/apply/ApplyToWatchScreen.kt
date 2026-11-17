package com.watchfacestudio.app.ui.screens.apply

import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.width
import com.watchfacestudio.app.WatchFaceStudioApp
import com.watchfacestudio.app.ui.appViewModel
import coil3.compose.AsyncImage
import com.watchfacestudio.app.ui.components.PrimaryButton
import com.watchfacestudio.app.ui.components.SecondaryButton
import com.watchfacestudio.app.ui.components.WatchPreview
import com.watchfacestudio.app.ui.theme.Background
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.Success
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun ApplyToWatchScreen(
    designId: String,
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit,
    onEditAgainClick: () -> Unit,
    viewModel: ApplyToWatchViewModel = appViewModel {
        ApplyToWatchViewModel(
            WatchFaceStudioApp.instance.container.designRepository,
            WatchFaceStudioApp.instance.container.deviceRepository,
            WatchFaceStudioApp.instance.container.watchFaceInstaller
        )
    }
) {
    val applyState by viewModel.applyState.collectAsState()
    val design by viewModel.currentDesign.collectAsState()
    val selectedDevice by viewModel.selectedDevice.collectAsState()
    val displayWidth = selectedDevice?.screenWidth?.takeIf { it > 0 } ?: (design?.canvasWidth ?: 0)
    val displayHeight = selectedDevice?.screenHeight?.takeIf { it > 0 } ?: (design?.canvasHeight ?: 0)
    val displayAspect = if (displayWidth > 0 && displayHeight > 0) displayWidth.toFloat() / displayHeight.toFloat() else null
    val displayShape = selectedDevice?.screenShape ?: design?.canvasShape ?: com.watchfacestudio.app.data.model.ScreenShape.RECTANGULAR

    LaunchedEffect(designId) {
        viewModel.startSync(designId)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var handoffMessage by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    var busy by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    fun handoff(share: Boolean) {
        val d = design ?: return
        if (busy) return
        busy = true
        scope.launch {
            try {
                val file = WatchFaceStudioApp.instance.container.watchFaceExporter.exportWatchFace(
                    d,
                    targetWidth = selectedDevice?.screenWidth?.takeIf { it > 0 },
                    targetHeight = selectedDevice?.screenHeight?.takeIf { it > 0 }
                )
                val uri = com.watchfacestudio.app.export.WatchFaceShare.saveToGallery(context, file)
                if (uri == null) {
                    handoffMessage = "Could not save the image."
                } else if (share) {
                    com.watchfacestudio.app.export.WatchFaceShare.shareImage(context, uri)
                    handoffMessage = null
                } else {
                    handoffMessage = "Saved to Pictures/WatchFaceStudio at ${selectedDevice?.screenWidth?.takeIf { it > 0 } ?: d.canvasWidth} × ${selectedDevice?.screenHeight?.takeIf { it > 0 } ?: d.canvasHeight}. Open your watch companion app and select this image as its watch face."
                }
            } catch (e: Exception) {
                handoffMessage = "Export failed: ${e.message}"
            } finally {
                busy = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Apply to Watch",
                style = WatchFaceTypography.titleMedium,
                color = TextPrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Watch Face Mockup
            WatchPreview(
                screenShape = displayShape,
                modifier = Modifier.size(width = 200.dp, height = if (displayAspect != null) (200f / displayAspect).coerceIn(100f, 400f).dp else 244.dp),
                bezelWidth = 8.dp,
                aspectRatioOverride = displayAspect
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (design?.imagePath != null) {
                        AsyncImage(
                            model = design?.imagePath,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "10:08",
                            style = WatchFaceTypography.headlineLarge.copy(fontSize = 32.sp),
                            color = Color.White
                        )
                        Text(
                            text = "Mon, Nov 16",
                            style = WatchFaceTypography.labelSmall,
                            color = Color(0xFFA78BFA)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            when (val state = applyState) {
                is ApplyState.InProgress -> {
                    CircularProgressIndicator(
                        color = Primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.statusMessage,
                        style = WatchFaceTypography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(6.dp)
                            .clip(CircleShape),
                        color = Primary,
                        trackColor = Color(0xFF222233)
                    )
                }

                is ApplyState.Completed -> {
                    // Green Checkmark Badge
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Success),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Watch Face Applied!",
                        style = WatchFaceTypography.headlineMedium,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = state.message,
                        style = WatchFaceTypography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    PrimaryButton(
                        text = "Done",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onDoneClick
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SecondaryButton(
                        text = "Edit Again",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onEditAgainClick
                    )
                }

                is ApplyState.Failed -> {
                    Text(
                        text = if (state.reason.contains("format", ignoreCase = true) || state.reason.contains("protocol", ignoreCase = true)) "Device Connected • Install Not Supported Yet" else "Unable to Apply Watch Face",
                        style = WatchFaceTypography.headlineMedium,
                        color = Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (state.reason.contains("format", ignoreCase = true) || state.reason.contains("protocol", ignoreCase = true)) "Your watch is connected. Its display size is shown when a verified device profile is available. Direct watch-face installation needs the manufacturer's supported format or companion app. You can still edit and export your design." else state.reason,
                        style = WatchFaceTypography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    PrimaryButton(
                        text = "Send to watch app",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { handoff(share = true) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SecondaryButton(
                        text = "Save image to gallery",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { handoff(share = false) }
                    )
                    handoffMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, style = WatchFaceTypography.bodySmall, color = TextSecondary, textAlign = TextAlign.Center)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    SecondaryButton(
                        text = "Back to Devices",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onBackClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SecondaryButton(
                        text = "Edit Design",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onEditAgainClick
                    )
                }
            }
        }
    }
}
