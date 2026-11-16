package com.watchfacestudio.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.data.model.ScreenShape

@Composable
fun WatchPreview(
    screenShape: ScreenShape = ScreenShape.RECTANGULAR,
    modifier: Modifier = Modifier,
    bezelWidth: Dp = 10.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val outerShape = when (screenShape) {
        ScreenShape.CIRCULAR -> CircleShape
        ScreenShape.SQUARE -> RoundedCornerShape(32.dp)
        ScreenShape.RECTANGULAR -> RoundedCornerShape(44.dp)
    }

    val innerShape = when (screenShape) {
        ScreenShape.CIRCULAR -> CircleShape
        ScreenShape.SQUARE -> RoundedCornerShape(22.dp)
        ScreenShape.RECTANGULAR -> RoundedCornerShape(34.dp)
    }

    val aspect = when (screenShape) {
        ScreenShape.CIRCULAR -> 1.0f
        ScreenShape.SQUARE -> 1.0f
        ScreenShape.RECTANGULAR -> 0.82f // modern tall smartwatch ratio like Apple Watch / Noise Ultra
    }

    Box(
        modifier = modifier
            .aspectRatio(aspect)
            .shadow(16.dp, outerShape, ambientColor = Color(0xFF8B5CF6), spotColor = Color(0xFF8B5CF6))
            .background(Color(0xFF1E1E28), outerShape)
            .border(
                width = 3.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF4A4A60), Color(0xFF1E1E28), Color(0xFF333345))
                ),
                shape = outerShape
            )
            .padding(bezelWidth),
        contentAlignment = Alignment.Center
    ) {
        // Watch display area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(innerShape)
                .background(Color.Black),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}
