package com.watchfacestudio.app.ui.screens.apply

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    LaunchedEffect(designId) {
        viewModel.startSync(designId)
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
                modifier = Modifier.size(200.dp),
                bezelWidth = 8.dp
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
                        text = "Synchronization Failed",
                        style = WatchFaceTypography.headlineMedium,
                        color = Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.reason,
                        style = WatchFaceTypography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    PrimaryButton(
                        text = "Retry",
                        onClick = { viewModel.startSync(designId) }
                    )
                }
            }
        }
    }
}
