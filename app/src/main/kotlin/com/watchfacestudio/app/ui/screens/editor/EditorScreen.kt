package com.watchfacestudio.app.ui.screens.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.watchfacestudio.app.ui.components.WatchPreview
import com.watchfacestudio.app.ui.screens.editor.components.WatchFaceCanvas
import com.watchfacestudio.app.ui.screens.editor.panels.BackgroundPanel
import com.watchfacestudio.app.ui.screens.editor.panels.DatePanel
import com.watchfacestudio.app.ui.screens.editor.panels.LayersPanel
import com.watchfacestudio.app.ui.screens.editor.panels.TimePanel
import com.watchfacestudio.app.ui.screens.editor.panels.WidgetsPanel
import com.watchfacestudio.app.ui.theme.Background
import com.watchfacestudio.app.ui.theme.BorderSubtle
import com.watchfacestudio.app.ui.theme.CardBackground
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun EditorScreen(
    imageUri: String? = null,
    designId: String? = null,
    onBackClick: () -> Unit,
    onApplyClick: (String) -> Unit,
    viewModel: EditorViewModel = appViewModel {
        EditorViewModel(
            WatchFaceStudioApp.instance.container.designRepository,
            WatchFaceStudioApp.instance.container.deviceRepository
        )
    }
) {
    val state by viewModel.state.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    LaunchedEffect(imageUri) {
        if (!imageUri.isNullOrBlank()) {
            viewModel.initFromImage(imageUri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Design Editor",
                style = WatchFaceTypography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { viewModel.undo() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    tint = TextSecondary
                )
            }
            IconButton(onClick = { viewModel.redo() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Redo,
                    contentDescription = "Redo",
                    tint = TextSecondary
                )
            }
            // Save & Apply Button
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Primary)
                    .clickable {
                        viewModel.saveDesign {
                            onApplyClick(state.designId)
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Apply",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Apply",
                        style = WatchFaceTypography.labelMedium,
                        color = Color.White
                    )
                }
            }
        }

        // Live Watch Canvas Preview (Centered)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            WatchPreview(
                screenShape = state.shape,
                modifier = Modifier.size(240.dp),
                bezelWidth = 10.dp
            ) {
                WatchFaceCanvas(state = state)
            }
        }

        // Toolbar: Time, Date, Widgets, Layers, BG
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SecondaryBackground)
                .border(1.dp, BorderSubtle, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val tabs = listOf(
                "Time" to Icons.Default.Schedule,
                "Date" to Icons.Default.CalendarToday,
                "Widgets" to Icons.Default.Widgets,
                "Layers" to Icons.Default.Layers,
                "BG" to Icons.Default.Image
            )
            tabs.forEach { (name, icon) ->
                val isSelected = selectedTab == name
                EditorToolbarItem(
                    title = name,
                    icon = icon,
                    isSelected = isSelected,
                    onClick = { viewModel.selectTab(name) }
                )
            }
        }

        // Selected Panel Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(CardBackground)
                .verticalScroll(rememberScrollState())
        ) {
            when (selectedTab) {
                "Time" -> TimePanel(
                    state = state,
                    onPositionChange = { viewModel.setClockPosition(it) },
                    onSizeChange = { viewModel.setClockSize(it) },
                    onFontChange = { viewModel.setClockFont(it) },
                    onColorChange = { viewModel.setClockColor(it) },
                    onOpacityChange = { viewModel.setClockOpacity(it) }
                )
                "Date" -> DatePanel(
                    state = state,
                    onColorChange = { /* update date color */ }
                )
                "Widgets" -> WidgetsPanel(
                    state = state,
                    onWidgetToggle = { viewModel.toggleWidget(it) }
                )
                "Layers" -> LayersPanel(
                    state = state,
                    onToggleVisibility = { viewModel.toggleLayerVisibility(it) },
                    onBringForward = { viewModel.moveLayerForward(it) },
                    onSendBackward = { viewModel.moveLayerBackward(it) }
                )
                "BG" -> BackgroundPanel(state = state)
            }
        }
    }
}

@Composable
fun EditorToolbarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Primary.copy(alpha = 0.2f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) Primary else TextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            style = WatchFaceTypography.labelSmall,
            color = if (isSelected) Primary else TextSecondary
        )
    }
}
