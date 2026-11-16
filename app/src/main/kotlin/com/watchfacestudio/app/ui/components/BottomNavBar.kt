package com.watchfacestudio.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.ui.theme.Background
import com.watchfacestudio.app.ui.theme.BorderSubtle
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

enum class BottomNavDestination(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    HOME("Home", Icons.Default.Home, "home"),
    CREATE("Create", Icons.Default.AddCircleOutline, "create"),
    MY_DESIGNS("My Designs", Icons.Default.Palette, "my_designs"),
    DEVICES("Devices", Icons.Default.Watch, "devices"),
    SETTINGS("Settings", Icons.Default.Settings, "settings")
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(SecondaryBackground)
            .border(width = 1.dp, color = BorderSubtle, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        containerColor = SecondaryBackground,
        tonalElevation = 8.dp
    ) {
        BottomNavDestination.entries.forEach { destination ->
            val selected = currentRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(destination.route) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title
                    )
                },
                label = {
                    Text(
                        text = destination.title,
                        style = WatchFaceTypography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = Primary.copy(alpha = 0.15f)
                )
            )
        }
    }
}
