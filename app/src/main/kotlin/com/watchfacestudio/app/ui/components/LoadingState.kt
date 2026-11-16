package com.watchfacestudio.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SurfaceVariant
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String = "Loading..."
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = Primary,
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = WatchFaceTypography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    height: Dp = 20.dp,
    width: Dp? = null
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            SurfaceVariant.copy(alpha = 0.3f),
            SurfaceVariant.copy(alpha = 0.7f),
            SurfaceVariant.copy(alpha = 0.3f)
        ),
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )

    val boxModifier = if (width != null) {
        modifier.size(width, height)
    } else {
        modifier.fillMaxWidth().height(height)
    }

    Box(modifier = boxModifier.clip(CircleShape).background(brush))
}
