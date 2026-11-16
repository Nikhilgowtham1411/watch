package com.watchfacestudio.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.ui.theme.CardBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun PermissionDialog(
    title: String,
    description: String,
    onGrant: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = {
            Text(
                text = title,
                style = WatchFaceTypography.titleMedium,
                color = TextPrimary
            )
        },
        text = {
            Text(
                text = description,
                style = WatchFaceTypography.bodyMedium,
                color = TextSecondary
            )
        },
        confirmButton = {
            PrimaryButton(
                text = "Allow",
                onClick = onGrant
            )
        },
        dismissButton = {
            SecondaryButton(
                text = "Not Now",
                onClick = onDismiss
            )
        }
    )
}
