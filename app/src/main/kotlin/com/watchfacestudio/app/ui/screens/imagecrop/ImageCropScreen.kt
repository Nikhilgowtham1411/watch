package com.watchfacestudio.app.ui.screens.imagecrop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.WatchFaceStudioApp
import com.watchfacestudio.app.ui.appViewModel
import coil3.compose.AsyncImage
import com.watchfacestudio.app.ui.components.PrimaryButton
import com.watchfacestudio.app.ui.theme.Background
import com.watchfacestudio.app.ui.theme.BorderSubtle
import com.watchfacestudio.app.ui.theme.CardBackground
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun ImageCropScreen(
    imageUri: String,
    onBackClick: () -> Unit,
    onNextClick: (String) -> Unit,
    viewModel: ImageCropViewModel = appViewModel {
        ImageCropViewModel(WatchFaceStudioApp.instance)
    }
) {
    val selectedRatio by viewModel.selectedRatio.collectAsState()
    val rotation by viewModel.rotationDegrees.collectAsState()
    val isFlipped by viewModel.isFlippedHorizontally.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

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
                text = "Edit Photo",
                style = WatchFaceTypography.titleMedium,
                color = TextPrimary
            )
        }

        // Image Preview with Grid & Crop Frame
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val aspect = selectedRatio.ratio ?: 1.0f
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(aspect)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .border(2.dp, Primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation)
                        .scale(scaleX = if (isFlipped) -1f else 1f, scaleY = 1f)
                )

                // 3x3 Rule of Thirds Grid Overlay
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Verticals
                    drawLine(Color.White.copy(alpha = 0.35f), Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = 1.dp.toPx())
                    drawLine(Color.White.copy(alpha = 0.35f), Offset(2f * w / 3f, 0f), Offset(2f * w / 3f, h), strokeWidth = 1.dp.toPx())
                    // Horizontals
                    drawLine(Color.White.copy(alpha = 0.35f), Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = 1.dp.toPx())
                    drawLine(Color.White.copy(alpha = 0.35f), Offset(0f, 2f * h / 3f), Offset(w, 2f * h / 3f), strokeWidth = 1.dp.toPx())
                }
            }
        }

        // Ratio Selectors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CropAspectRatio.entries.forEach { ratio ->
                val isSelected = ratio == selectedRatio
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Primary else SecondaryBackground)
                        .clickable { viewModel.setAspectRatio(ratio) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = ratio.label,
                        style = WatchFaceTypography.labelMedium,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tool Action Buttons: Rotate, Flip, Reset
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CropActionButton(
                label = "Rotate",
                icon = Icons.Default.RotateRight,
                onClick = { viewModel.rotate90() }
            )
            CropActionButton(
                label = "Flip",
                icon = Icons.Default.Flip,
                onClick = { viewModel.flipHorizontal() }
            )
            CropActionButton(
                label = "Reset",
                icon = Icons.Default.Refresh,
                onClick = { viewModel.reset() }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Next Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    color = Primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                PrimaryButton(
                    text = "Next",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.processAndSave(imageUri) { processedPath ->
                            onNextClick(processedPath)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CropActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SecondaryBackground)
                .border(1.dp, BorderSubtle, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = WatchFaceTypography.labelSmall,
            color = TextSecondary
        )
    }
}
