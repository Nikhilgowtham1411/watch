package com.watchfacestudio.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.ui.theme.CardBackground
import com.watchfacestudio.app.ui.theme.GlassBackground
import com.watchfacestudio.app.ui.theme.GlassBorder
import com.watchfacestudio.app.ui.theme.WatchFaceShapes

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = WatchFaceShapes.medium,
    borderWidth: Dp = 1.dp,
    blurRadius: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = CardBackground.copy(alpha = 0.65f)
        ),
        border = BorderStroke(borderWidth, GlassBorder)
    ) {
        Box(
            modifier = Modifier
                .background(GlassBackground)
                .padding(16.dp),
            content = content
        )
    }
}
