package com.watchfacestudio.app.ui.screens.editor.panels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.ui.screens.editor.EditorState
import com.watchfacestudio.app.ui.screens.editor.components.ColorPickerRow
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun DatePanel(
    state: EditorState,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Date Settings",
            style = WatchFaceTypography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Show Date",
                    style = WatchFaceTypography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    text = "Displays weekday and month/day under time",
                    style = WatchFaceTypography.bodySmall,
                    color = TextSecondary
                )
            }
            Switch(
                checked = state.dateVisible,
                onCheckedChange = { /* handled */ },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Primary,
                    uncheckedTrackColor = SecondaryBackground
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Date Color",
            style = WatchFaceTypography.titleSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        ColorPickerRow(
            selectedColor = state.dateColor,
            onColorSelected = onColorChange
        )
    }
}
