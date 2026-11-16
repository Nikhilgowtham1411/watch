package com.watchfacestudio.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val WatchFaceDarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = TertiaryAccent,

    secondary = SecondaryAccent,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceVariant,
    onSecondaryContainer = SecondaryAccent,

    tertiary = TertiaryAccent,
    onTertiary = Background,
    tertiaryContainer = SurfaceBright,
    onTertiaryContainer = TertiaryAccent,

    background = Background,
    onBackground = TextPrimary,

    surface = SecondaryBackground,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceTint = Primary,

    error = Error,
    onError = TextPrimary,
    errorContainer = ErrorContainer,
    onErrorContainer = Error,

    outline = Border,
    outlineVariant = BorderSubtle,
    inverseSurface = TextPrimary,
    inverseOnSurface = Background,
    inversePrimary = PrimaryVariant,
    scrim = Overlay
)

@Composable
fun WatchFaceStudioTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = WatchFaceDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = Background.toArgb()
            window.navigationBarColor = Background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WatchFaceTypography,
        shapes = WatchFaceShapes,
        content = content
    )
}
