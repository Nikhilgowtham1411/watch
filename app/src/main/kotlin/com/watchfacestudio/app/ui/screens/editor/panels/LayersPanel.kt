package com.watchfacestudio.app.ui.screens.editor.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.data.model.LayerType
import com.watchfacestudio.app.ui.components.SecondaryButton
import com.watchfacestudio.app.ui.screens.editor.EditorState
import com.watchfacestudio.app.ui.screens.editor.UiLayer
import com.watchfacestudio.app.ui.theme.CardBackground
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun LayersPanel(
    state: EditorState,
    onToggleVisibility: (String) -> Unit,
    onBringForward: (String) -> Unit,
    onSendBackward: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLayerId by remember { mutableStateOf(state.layers.firstOrNull()?.id.orEmpty()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Layers",
            style = WatchFaceTypography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            state.layers.forEach { layer ->
                val isSelected = layer.id == selectedLayerId
                LayerRowItem(
                    layer = layer,
                    isSelected = isSelected,
                    onClick = { selectedLayerId = layer.id },
                    onToggleVisibility = { onToggleVisibility(layer.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bring Forward / Send Backward Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryButton(
                text = "Bring Forward",
                icon = Icons.Default.ArrowUpward,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (selectedLayerId.isNotEmpty()) onBringForward(selectedLayerId)
                }
            )
            SecondaryButton(
                text = "Send Backward",
                icon = Icons.Default.ArrowDownward,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (selectedLayerId.isNotEmpty()) onSendBackward(selectedLayerId)
                }
            )
        }
    }
}

@Composable
fun LayerRowItem(
    layer: UiLayer,
    isSelected: Boolean,
    onClick: () -> Unit,
    onToggleVisibility: () -> Unit
) {
    val icon: ImageVector = when (layer.type) {
        LayerType.DIGITAL_TIME, LayerType.ANALOG_CLOCK -> Icons.Default.Schedule
        LayerType.DATE -> Icons.Default.CalendarToday
        LayerType.FOREGROUND_SUBJECT -> Icons.Default.Layers
        LayerType.BACKGROUND_IMAGE -> Icons.Default.Image
        else -> Icons.Default.Layers
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Primary.copy(alpha = 0.15f) else CardBackground)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) Primary else Color(0x22FFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Primary else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = layer.name,
                style = WatchFaceTypography.titleSmall,
                color = if (layer.visible) TextPrimary else TextSecondary.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f)
            )

            if (layer.locked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            IconButton(
                onClick = onToggleVisibility,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (layer.visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle Visibility",
                    tint = if (layer.visible) Primary else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = "Reorder",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
