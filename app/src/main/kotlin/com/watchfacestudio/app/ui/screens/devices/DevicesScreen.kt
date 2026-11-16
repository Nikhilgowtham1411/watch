package com.watchfacestudio.app.ui.screens.devices

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.WatchFaceStudioApp
import com.watchfacestudio.app.ui.appViewModel
import com.watchfacestudio.app.bluetooth.model.BluetoothState
import com.watchfacestudio.app.bluetooth.model.DiscoveredDevice
import com.watchfacestudio.app.ui.components.GlassCard
import com.watchfacestudio.app.ui.theme.Background
import com.watchfacestudio.app.ui.theme.CardBackground
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.Success
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun DevicesScreen(
    onBackClick: () -> Unit,
    viewModel: DevicesViewModel = appViewModel {
        DevicesViewModel(
            WatchFaceStudioApp.instance.container.bleScanner,
            WatchFaceStudioApp.instance.container.bleConnectionManager,
            WatchFaceStudioApp.instance.container.permissionHelper,
            WatchFaceStudioApp.instance.container.bluetoothStateMonitor,
            WatchFaceStudioApp.instance.container.deviceRepository
        )
    }
) {
    val isScanning by viewModel.isScanning.collectAsState()
    val discoveredDevices by viewModel.discoveredDevices.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val bluetoothState by viewModel.bluetoothState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.all { it }) {
            viewModel.startScanning()
        }
    }

    LaunchedEffect(Unit) {
        if (viewModel.hasPermissions()) {
            viewModel.startScanning()
        } else {
            permissionLauncher.launch(viewModel.getRequiredPermissions().toTypedArray())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Connect Device",
                style = WatchFaceTypography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    if (viewModel.hasPermissions()) {
                        viewModel.startScanning()
                    } else {
                        permissionLauncher.launch(viewModel.getRequiredPermissions().toTypedArray())
                    }
                }
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Primary
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Radar Animation Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RadarPulseAnimation(isScanning = isScanning)

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (isScanning) "Searching for nearby Bluetooth devices..." else "Device Discovery Ready",
                        style = WatchFaceTypography.titleMedium,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Nearby discoverable BLE devices will appear here.\nSome devices may not advertise while idle.",
                        style = WatchFaceTypography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Bluetooth State Notice if disabled
            if (bluetoothState == BluetoothState.DISABLED) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(
                                text = "Bluetooth is currently OFF",
                                style = WatchFaceTypography.titleSmall,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Turn on Bluetooth to discover nearby devices.",
                                style = WatchFaceTypography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Only display results received from the real BLE scanner.
            if (discoveredDevices.isEmpty() && !isScanning && bluetoothState != BluetoothState.DISABLED) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No nearby devices found", style = WatchFaceTypography.titleSmall, color = TextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Make the device discoverable and scan again.",
                                style = WatchFaceTypography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            items(discoveredDevices, key = { it.address }) { device ->
                val connectedDevice = connectionState as? com.watchfacestudio.app.bluetooth.connection.ConnectionState.Connected
                val isConnected = connectedDevice?.device?.address == device.address
                DeviceCardItem(
                    name = device.name?.takeIf { it.isNotBlank() } ?: "Unknown BLE device",
                    address = device.address,
                    rssi = device.rssi,
                    isConnected = isConnected,
                    onClick = { viewModel.connectToDevice(device) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun RadarPulseAnimation(isScanning: Boolean) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier.size(130.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow circle
        Box(
            modifier = Modifier
                .size(130.dp)
                .scale(if (isScanning) scale else 1.0f)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.10f))
        )
        // Mid ring
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.20f))
        )
        // Center circle with Watch icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun DeviceCardItem(
    name: String,
    address: String,
    rssi: Int,
    isConnected: Boolean,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SecondaryBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Watch,
                    contentDescription = null,
                    tint = if (isConnected) Primary else TextSecondary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = WatchFaceTypography.titleSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = if (isConnected) "Connected • RSSI $rssi dBm" else "RSSI $rssi dBm • Tap to connect",
                    style = WatchFaceTypography.bodySmall,
                    color = if (isConnected) Success else TextSecondary
                )
            }

            if (isConnected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Connected",
                    tint = Success,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Connect",
                    tint = TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
