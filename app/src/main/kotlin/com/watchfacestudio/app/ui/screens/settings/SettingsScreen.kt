package com.watchfacestudio.app.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.WatchFaceStudioApp
import com.watchfacestudio.app.ui.appViewModel
import com.watchfacestudio.app.ui.components.GlassCard
import com.watchfacestudio.app.ui.theme.Background
import com.watchfacestudio.app.ui.theme.CardBackground
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = appViewModel {
        SettingsViewModel(
            WatchFaceStudioApp.instance,
            WatchFaceStudioApp.instance.container.preferences
        )
    }
) {
    val imageQuality by viewModel.imageQuality.collectAsState()
    val autoReconnect by viewModel.autoReconnect.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = WatchFaceTypography.headlineMedium,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Bluetooth Section
        SettingsSectionHeader(title = "Connectivity")
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto Reconnect",
                        style = WatchFaceTypography.titleSmall,
                        color = TextPrimary
                    )
                    Text(
                        text = "Reconnect when smartwatch is in range",
                        style = WatchFaceTypography.bodySmall,
                        color = TextSecondary
                    )
                }
                Switch(
                    checked = autoReconnect,
                    onCheckedChange = { viewModel.toggleAutoReconnect(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Primary,
                        uncheckedTrackColor = SecondaryBackground
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Image Quality
        SettingsSectionHeader(title = "Export & Rendering")
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column {
                SettingsOptionRow(
                    icon = Icons.Default.HighQuality,
                    title = "Export Quality",
                    value = imageQuality,
                    onClick = {
                        val next = when (imageQuality) {
                            "HIGH" -> "MEDIUM"
                            "MEDIUM" -> "LOW"
                            else -> "HIGH"
                        }
                        viewModel.setImageQuality(next)
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
                SettingsOptionRow(
                    icon = Icons.Default.CleaningServices,
                    title = "Clear Image Cache",
                    value = "Free space",
                    onClick = {
                        viewModel.clearCache {
                            Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // General
        SettingsSectionHeader(title = "App Preferences")
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column {
                SettingsOptionRow(
                    icon = Icons.Default.Palette,
                    title = "App Theme",
                    value = "Dark / Glassmorphism",
                    onClick = {}
                )
                Spacer(modifier = Modifier.height(14.dp))
                SettingsOptionRow(
                    icon = Icons.Default.Language,
                    title = "Language",
                    value = "English",
                    onClick = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // About
        SettingsSectionHeader(title = "About & Privacy")
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column {
                SettingsOptionRow(
                    icon = Icons.Default.Security,
                    title = "Privacy & Local Storage",
                    value = "100% on-device",
                    onClick = {}
                )
                Spacer(modifier = Modifier.height(14.dp))
                SettingsOptionRow(
                    icon = Icons.Default.Info,
                    title = "Version",
                    value = "1.0.0 (Release)",
                    onClick = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = WatchFaceTypography.labelMedium,
        color = Primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsOptionRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = WatchFaceTypography.titleSmall,
                color = TextPrimary
            )
            Text(
                text = value,
                style = WatchFaceTypography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
