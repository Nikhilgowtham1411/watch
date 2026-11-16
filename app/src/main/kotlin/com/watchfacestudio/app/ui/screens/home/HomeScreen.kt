package com.watchfacestudio.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.WatchFaceStudioApp
import com.watchfacestudio.app.ui.appViewModel
import com.watchfacestudio.app.data.local.db.entities.DesignEntity
import com.watchfacestudio.app.ui.components.GlassCard
import com.watchfacestudio.app.ui.components.PrimaryButton
import com.watchfacestudio.app.ui.components.SectionHeader
import com.watchfacestudio.app.ui.components.StatusChip
import com.watchfacestudio.app.ui.components.StatusType
import com.watchfacestudio.app.ui.components.TopAppBar
import com.watchfacestudio.app.ui.components.WatchPreview
import com.watchfacestudio.app.ui.theme.Background
import com.watchfacestudio.app.ui.theme.CardBackground
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.Success
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceShapes
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun HomeScreen(
    onCreateClick: () -> Unit,
    onDesignClick: (String) -> Unit,
    onDevicesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMyDesignsClick: () -> Unit,
    viewModel: HomeViewModel = appViewModel {
        HomeViewModel(
            WatchFaceStudioApp.instance.container.designRepository,
            WatchFaceStudioApp.instance.container.deviceRepository
        )
    }
) {
    val recentDesigns by viewModel.recentDesigns.collectAsState()
    val primaryDevice by viewModel.primaryDevice.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        TopAppBar()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // A. Connected Device Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDevicesClick() },
                shape = WatchFaceShapes.large
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Watch Thumbnail
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SecondaryBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Watch,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = primaryDevice?.name ?: "No Watch Connected",
                            style = WatchFaceTypography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (primaryDevice != null) Success else TextSecondary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (primaryDevice != null) "Connected • BLE" else "Tap to pair",
                                style = WatchFaceTypography.labelSmall,
                                color = if (primaryDevice != null) Success else TextSecondary
                            )
                            if (primaryDevice != null) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(
                                    imageVector = Icons.Default.BatteryFull,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "78%",
                                    style = WatchFaceTypography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Manage",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // B. Main Create Button
            PrimaryButton(
                text = "Create Watch Face",
                icon = Icons.Default.Add,
                modifier = Modifier.fillMaxWidth(),
                onClick = onCreateClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // C. Quick Actions Grid (2x2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Templates",
                    icon = Icons.Default.ViewModule,
                    modifier = Modifier.weight(1f),
                    onClick = onCreateClick
                )
                QuickActionCard(
                    title = "My Designs",
                    icon = Icons.Default.Palette,
                    modifier = Modifier.weight(1f),
                    onClick = onMyDesignsClick
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Devices",
                    icon = Icons.Default.Devices,
                    modifier = Modifier.weight(1f),
                    onClick = onDevicesClick
                )
                QuickActionCard(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    modifier = Modifier.weight(1f),
                    onClick = onSettingsClick
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // D. Recent Designs Section
            SectionHeader(
                title = "Recent Designs",
                actionText = "See All",
                onActionClick = onMyDesignsClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (recentDesigns.isEmpty()) {
                // Pre-canned mock previews for instant visual appeal
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(listOf("Mountain Sunset", "Neon Cyan", "Minimal AMOLED", "Cosmic Purple")) { title ->
                        MockDesignCard(title = title, onClick = onCreateClick)
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recentDesigns) { design ->
                        RealDesignCard(design = design, onClick = { onDesignClick(design.id) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // E. Watch Compatibility Information
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = WatchFaceShapes.large
            ) {
                Column {
                    Text(
                        text = "Compatibility Engine",
                        style = WatchFaceTypography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Support for Noise, boAt, Fire-Boltt, Samsung Galaxy Watch (Wear OS), and Amazfit devices. Custom resolution and safe-area scaling enabled.",
                        style = WatchFaceTypography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // padding for bottom nav
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier.clickable { onClick() },
        shape = WatchFaceShapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = WatchFaceTypography.titleSmall,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun MockDesignCard(
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 130.dp, height = 160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            WatchPreview(
                modifier = Modifier.size(80.dp),
                bezelWidth = 6.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "10:08",
                        style = WatchFaceTypography.labelSmall,
                        color = Color.White
                    )
                    Text(
                        text = "Mon 16",
                        style = WatchFaceTypography.labelSmall.copy(fontSize = androidx.compose.ui.unit.TextUnit.Unspecified),
                        color = Primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = WatchFaceTypography.labelSmall,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun RealDesignCard(
    design: DesignEntity,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 130.dp, height = 160.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            WatchPreview(
                screenShape = design.canvasShape,
                modifier = Modifier.size(80.dp),
                bezelWidth = 6.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "10:08",
                        style = WatchFaceTypography.labelSmall,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = design.name,
                style = WatchFaceTypography.labelSmall,
                color = TextPrimary
            )
        }
    }
}
