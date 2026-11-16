package com.watchfacestudio.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryAccent
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.WatchFaceShapes

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fullWidth: Boolean = true,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = WatchFaceShapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            disabledContainerColor = Primary.copy(alpha = 0.4f),
            disabledContentColor = TextPrimary.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(),
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .height(52.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    brush = if (enabled) {
                        Brush.horizontalGradient(listOf(Primary, SecondaryAccent))
                    } else {
                        Brush.horizontalGradient(
                            listOf(
                                Primary.copy(alpha = 0.4f),
                                SecondaryAccent.copy(alpha = 0.4f)
                            )
                        )
                    },
                    shape = WatchFaceShapes.medium
                )
                .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
                .height(52.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(20.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }
    }
}
