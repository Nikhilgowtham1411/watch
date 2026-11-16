package com.watchfacestudio.app.ui.screens.editor.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogClockRenderer(
    handColor: Color = Color.White,
    secondHandColor: Color = Color(0xFFEF4444),
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f

        // Draw hour markers (12 ticks)
        for (i in 0 until 12) {
            val angle = Math.toRadians((i * 30.0) - 90.0)
            val outer = Offset(
                center.x + (radius - 8.dp.toPx()) * cos(angle).toFloat(),
                center.y + (radius - 8.dp.toPx()) * sin(angle).toFloat()
            )
            val inner = Offset(
                center.x + (radius - 18.dp.toPx()) * cos(angle).toFloat(),
                center.y + (radius - 18.dp.toPx()) * sin(angle).toFloat()
            )
            drawLine(
                color = handColor.copy(alpha = 0.6f),
                start = inner,
                end = outer,
                strokeWidth = if (i % 3 == 0) 3.dp.toPx() else 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Clock time (e.g. 10:08:42)
        val hours = 10
        val minutes = 8
        val seconds = 42

        // Hour Hand
        val hourAngle = Math.toRadians(((hours % 12 + minutes / 60.0) * 30.0) - 90.0)
        val hourHandEnd = Offset(
            center.x + (radius * 0.5f) * cos(hourAngle).toFloat(),
            center.y + (radius * 0.5f) * sin(hourAngle).toFloat()
        )
        drawLine(
            color = handColor,
            start = center,
            end = hourHandEnd,
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Minute Hand
        val minuteAngle = Math.toRadians((minutes * 6.0) - 90.0)
        val minuteHandEnd = Offset(
            center.x + (radius * 0.72f) * cos(minuteAngle).toFloat(),
            center.y + (radius * 0.72f) * sin(minuteAngle).toFloat()
        )
        drawLine(
            color = handColor,
            start = center,
            end = minuteHandEnd,
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Second Hand
        val secondAngle = Math.toRadians((seconds * 6.0) - 90.0)
        val secondHandEnd = Offset(
            center.x + (radius * 0.82f) * cos(secondAngle).toFloat(),
            center.y + (radius * 0.82f) * sin(secondAngle).toFloat()
        )
        drawLine(
            color = secondHandColor,
            start = center,
            end = secondHandEnd,
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Center Pin
        drawCircle(color = handColor, radius = 5.dp.toPx(), center = center)
        drawCircle(color = secondHandColor, radius = 2.5.dp.toPx(), center = center)
    }
}
