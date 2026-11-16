package com.watchfacestudio.app.ui.screens.editor.panels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.ui.screens.editor.EditorState
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun BackgroundPanel(
    state: EditorState,
    onBlurChange: (Float) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Background Adjustments",
            style = WatchFaceTypography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Blur Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Blur Effect",
                style = WatchFaceTypography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = "${state.bgBlur.toInt()}px",
                style = WatchFaceTypography.labelMedium,
                color = Primary
            )
        }
        Slider(
            value = state.bgBlur,
            onValueChange = onBlurChange,
            valueRange = 0f..25f,
            colors = SliderDefaults.colors(
                thumbColor = Primary,
                activeTrackColor = Primary,
                inactiveTrackColor = SecondaryBackground
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Brightness Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Brightness",
                style = WatchFaceTypography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = "${(state.bgBrightness * 100).toInt()}%",
                style = WatchFaceTypography.labelMedium,
                color = Primary
            )
        }
        Slider(
            value = state.bgBrightness,
            onValueChange = onBrightnessChange,
            valueRange = 0.4f..1.4f,
            colors = SliderDefaults.colors(
                thumbColor = Primary,
                activeTrackColor = Primary,
                inactiveTrackColor = SecondaryBackground
            )
        )
    }
}
