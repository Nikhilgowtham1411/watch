package com.watchfacestudio.app.ui.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import com.watchfacestudio.app.ui.theme.AppFontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.watchfacestudio.app.data.model.WidgetType
import com.watchfacestudio.app.ui.screens.editor.ClockPosition
import com.watchfacestudio.app.ui.screens.editor.EditorState

@Composable
fun WatchFaceCanvas(
    state: EditorState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Background Image or Gradient Layer
        val bgLayer = state.layers.find { it.name.contains("Background") }
        if (bgLayer?.visible != false) {
            if (state.imagePath != null) {
                AsyncImage(
                    model = state.imagePath,
                    contentDescription = "Watch Face Background",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
                        state.bgBrightness, 0f, 0f, 0f, 0f,
                        0f, state.bgBrightness, 0f, 0f, 0f,
                        0f, 0f, state.bgBrightness, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    ))),
                    modifier = Modifier.fillMaxSize().blur(state.bgBlur.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E1035), Color(0xFF0F0B18), Color(0xFF07050A))
                            )
                        )
                )
            }
        }

        // 2. Clock Layer (Digital or Analog)
        val timeLayer = state.layers.find { it.name.contains("Time") }
        if (timeLayer?.visible != false) {
            if (state.isAnalog) {
                AnalogClockRenderer(
                    handColor = state.clockColor,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val alignment = when (state.clockPosition) {
                    ClockPosition.TOP_LEFT -> Alignment.TopStart
                    ClockPosition.TOP_CENTER -> Alignment.TopCenter
                    ClockPosition.TOP_RIGHT -> Alignment.TopEnd
                    ClockPosition.CENTER_LEFT -> Alignment.CenterStart
                    ClockPosition.CENTER -> Alignment.Center
                    ClockPosition.CENTER_RIGHT -> Alignment.CenterEnd
                    ClockPosition.BOTTOM_LEFT -> Alignment.BottomStart
                    ClockPosition.BOTTOM_CENTER -> Alignment.BottomCenter
                    ClockPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
                }

                val font = when (state.clockFont) {
                    "Bold" -> AppFontFamily
                    "Mono" -> FontFamily.Monospace
                    "Serif" -> FontFamily.Serif
                    else -> AppFontFamily
                }

                val baseFontSize = 40.sp * (state.clockSizePercent / 100f)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = alignment
                ) {
                    Column(
                        horizontalAlignment = when (state.clockPosition) {
                            ClockPosition.TOP_LEFT, ClockPosition.CENTER_LEFT, ClockPosition.BOTTOM_LEFT -> Alignment.Start
                            ClockPosition.TOP_RIGHT, ClockPosition.CENTER_RIGHT, ClockPosition.BOTTOM_RIGHT -> Alignment.End
                            else -> Alignment.CenterHorizontally
                        }
                    ) {
                        Text(
                            text = "10:08",
                            fontSize = baseFontSize,
                            fontWeight = FontWeight.Bold,
                            fontFamily = font,
                            color = state.clockColor.copy(alpha = state.clockOpacity),
                            textAlign = TextAlign.Center
                        )

                        // Date display right beneath or aligned with clock
                        val dateLayer = state.layers.find { it.name.contains("Date") }
                        if (dateLayer?.visible != false && state.dateVisible) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Mon, Nov 16",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = state.dateColor.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // 3. Widgets Layer (Steps, Battery, Heart Rate, etc.)
        val enabledWidgets = state.widgets.filter { it.enabled }
        if (enabledWidgets.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    enabledWidgets.take(3).forEach { w ->
                        WidgetBadge(widget = w)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetBadge(widget: com.watchfacestudio.app.ui.screens.editor.UiWidget) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.45f), shape = androidx.compose.foundation.shape.CircleShape)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        val icon = when (widget.type) {
            WidgetType.STEPS -> Icons.Default.DirectionsWalk
            WidgetType.HEARTRATE -> Icons.Default.Favorite
            WidgetType.BATTERY -> Icons.Default.BatteryFull
            WidgetType.WEATHER -> Icons.Default.WbSunny
            else -> Icons.Default.BatteryFull
        }
        val tint = when (widget.type) {
            WidgetType.HEARTRATE -> Color(0xFFEF4444)
            WidgetType.STEPS -> Color(0xFF22C55E)
            WidgetType.BATTERY -> Color(0xFF38BDF8)
            WidgetType.WEATHER -> Color(0xFFFBBF24)
            else -> Color.White
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = widget.previewValue,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}
