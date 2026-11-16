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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.data.model.WidgetType
import com.watchfacestudio.app.ui.screens.editor.EditorState
import com.watchfacestudio.app.ui.screens.editor.UiWidget
import com.watchfacestudio.app.ui.theme.CardBackground
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun WidgetsPanel(
    state: EditorState,
    onWidgetToggle: (WidgetType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Widgets",
            style = WatchFaceTypography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.height(280.dp)
        ) {
            items(state.widgets) { widget ->
                WidgetGridItem(
                    widget = widget,
                    onClick = { onWidgetToggle(widget.type) }
                )
            }
        }
    }
}

@Composable
fun WidgetGridItem(
    widget: UiWidget,
    onClick: () -> Unit
) {
    val icon: ImageVector = when (widget.type) {
        WidgetType.DATE -> Icons.Default.CalendarToday
        WidgetType.STEPS -> Icons.Default.DirectionsWalk
        WidgetType.HEARTRATE -> Icons.Default.Favorite
        WidgetType.BATTERY -> Icons.Default.BatteryFull
        WidgetType.DAY -> Icons.Default.CalendarMonth
        WidgetType.WEATHER -> Icons.Default.WbSunny
        WidgetType.CALORIES -> Icons.Default.LocalFireDepartment
        WidgetType.DISTANCE -> Icons.Default.LocationOn
        else -> Icons.Default.CalendarToday
    }

    val iconColor = when (widget.type) {
        WidgetType.HEARTRATE -> Color(0xFFEF4444)
        WidgetType.STEPS -> Color(0xFF22C55E)
        WidgetType.BATTERY -> Color(0xFF38BDF8)
        WidgetType.WEATHER -> Color(0xFFFBBF24)
        WidgetType.CALORIES -> Color(0xFFFB923C)
        WidgetType.DISTANCE -> Color(0xFFA78BFA)
        else -> Primary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .border(
                width = if (widget.enabled) 1.5.dp else 1.dp,
                color = if (widget.enabled) Primary else Color(0x22FFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = widget.name,
                    style = WatchFaceTypography.labelMedium,
                    color = TextPrimary
                )
                Text(
                    text = widget.previewValue,
                    style = WatchFaceTypography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
