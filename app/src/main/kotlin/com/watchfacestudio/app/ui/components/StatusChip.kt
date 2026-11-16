package com.watchfacestudio.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.ui.theme.Success
import com.watchfacestudio.app.ui.theme.SuccessContainer
import com.watchfacestudio.app.ui.theme.Warning
import com.watchfacestudio.app.ui.theme.WarningContainer
import com.watchfacestudio.app.ui.theme.Error
import com.watchfacestudio.app.ui.theme.ErrorContainer
import com.watchfacestudio.app.ui.theme.Info
import com.watchfacestudio.app.ui.theme.InfoContainer
import com.watchfacestudio.app.ui.theme.WatchFaceShapes

enum class ChipStatus(val contentColor: Color, val containerColor: Color) {
    Connected(Success, SuccessContainer),
    Scanning(Info, InfoContainer),
    Disconnected(Error, ErrorContainer),
    Warning(com.watchfacestudio.app.ui.theme.Warning, WarningContainer)
}

typealias StatusType = ChipStatus

@Composable
fun StatusChip(
    label: String,
    status: ChipStatus,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Row(
        modifier = modifier
            .clip(WatchFaceShapes.small)
            .background(status.containerColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Small status dot
        androidx.compose.foundation.Canvas(
            modifier = Modifier.size(6.dp)
        ) {
            drawCircle(color = status.contentColor)
        }

        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = status.contentColor,
                modifier = Modifier.size(14.dp)
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = status.contentColor
        )
    }
}
