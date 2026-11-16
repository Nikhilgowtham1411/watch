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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watchfacestudio.app.ui.components.GlassCard
import com.watchfacestudio.app.ui.screens.editor.ClockPosition
import com.watchfacestudio.app.ui.screens.editor.EditorState
import com.watchfacestudio.app.ui.screens.editor.components.ColorPickerRow
import com.watchfacestudio.app.ui.theme.CardBackground
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun TimePanel(
    state: EditorState,
    onPositionChange: (ClockPosition) -> Unit,
    onSizeChange: (Int) -> Unit,
    onFontChange: (String) -> Unit,
    onColorChange: (Color) -> Unit,
    onOpacityChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Preview Header Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "10:08",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = state.clockColor.copy(alpha = state.clockOpacity)
                )
                Text(
                    text = "Mon, Nov 16",
                    style = WatchFaceTypography.labelSmall,
                    color = state.dateColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Position Section (3x3 Grid)
        Text(
            text = "Position",
            style = WatchFaceTypography.titleSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBackground)
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val rows = listOf(
                        listOf(ClockPosition.TOP_LEFT, ClockPosition.TOP_CENTER, ClockPosition.TOP_RIGHT),
                        listOf(ClockPosition.CENTER_LEFT, ClockPosition.CENTER, ClockPosition.CENTER_RIGHT),
                        listOf(ClockPosition.BOTTOM_LEFT, ClockPosition.BOTTOM_CENTER, ClockPosition.BOTTOM_RIGHT)
                    )
                    rows.forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            row.forEach { pos ->
                                val isSelected = pos == state.clockPosition
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Primary else Color(0xFF4A4A60))
                                        .clickable { onPositionChange(pos) }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Size Section (Slider)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Size",
                style = WatchFaceTypography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = "${state.clockSizePercent}%",
                style = WatchFaceTypography.labelMedium,
                color = Primary
            )
        }
        Slider(
            value = state.clockSizePercent.toFloat(),
            onValueChange = { onSizeChange(it.toInt()) },
            valueRange = 70f..180f,
            colors = SliderDefaults.colors(
                thumbColor = Primary,
                activeTrackColor = Primary,
                inactiveTrackColor = SecondaryBackground
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Font Section
        Text(
            text = "Font",
            style = WatchFaceTypography.titleSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val fonts = listOf(
                "Modern" to "Aa",
                "Bold" to "Aa",
                "Mono" to "Aa",
                "Serif" to "Aa"
            )
            fonts.forEach { (name, label) ->
                val isSelected = state.clockFont == name
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Primary else SecondaryBackground)
                        .clickable { onFontChange(name) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontWeight = if (name == "Bold") FontWeight.Black else FontWeight.Normal,
                        fontFamily = when (name) {
                            "Mono" -> FontFamily.Monospace
                            "Serif" -> FontFamily.Serif
                            else -> FontFamily.Default
                        },
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Color Section
        Text(
            text = "Color",
            style = WatchFaceTypography.titleSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        ColorPickerRow(
            selectedColor = state.clockColor,
            onColorSelected = onColorChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Opacity Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Opacity",
                style = WatchFaceTypography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = "${(state.clockOpacity * 100).toInt()}%",
                style = WatchFaceTypography.labelMedium,
                color = Primary
            )
        }
        Slider(
            value = state.clockOpacity,
            onValueChange = onOpacityChange,
            valueRange = 0.2f..1.0f,
            colors = SliderDefaults.colors(
                thumbColor = Primary,
                activeTrackColor = Primary,
                inactiveTrackColor = SecondaryBackground
            )
        )
    }
}
