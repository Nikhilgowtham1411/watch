package com.watchfacestudio.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Background & Surface ────────────────────────────────────────────
val Background = Color(0xFF09090F)
val SecondaryBackground = Color(0xFF14141E)
val CardBackground = Color(0xFF1C1C29)
val SurfaceVariant = Color(0xFF252536)
val SurfaceBright = Color(0xFF2E2E42)
val Overlay = Color(0x8009090F) // 50 % opacity background

// ── Primary / Accent ────────────────────────────────────────────────
val Primary = Color(0xFF8B5CF6)
val PrimaryVariant = Color(0xFF7C3AED)
val SecondaryAccent = Color(0xFFA78BFA)
val TertiaryAccent = Color(0xFFC4B5FD)

// ── Text ────────────────────────────────────────────────────────────
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA1A1AA)
val TextTertiary = Color(0xFF71717A)
val TextDisabled = Color(0xFF52525B)

// ── Status ──────────────────────────────────────────────────────────
val Success = Color(0xFF22C55E)
val SuccessContainer = Color(0xFF052E16)
val Error = Color(0xFFEF4444)
val ErrorContainer = Color(0xFF450A0A)
val Warning = Color(0xFFF59E0B)
val WarningContainer = Color(0xFF451A03)
val Info = Color(0xFF3B82F6)
val InfoContainer = Color(0xFF172554)

// ── Border / Divider ────────────────────────────────────────────────
val Border = Color(0xFF2A2A3D)
val BorderSubtle = Color(0xFF1F1F30)
val DividerColor = Color(0xFF27273A)

// ── Glass effect ────────────────────────────────────────────────────
val GlassBorder = Color(0x33FFFFFF) // 20 % white
val GlassBackground = Color(0x1AFFFFFF) // 10 % white

// ── Gradients ───────────────────────────────────────────────────────
val PrimaryGradient = Brush.horizontalGradient(
    colors = listOf(Primary, SecondaryAccent)
)

val PrimaryVerticalGradient = Brush.verticalGradient(
    colors = listOf(Primary, SecondaryAccent)
)

val BackgroundGradient = Brush.verticalGradient(
    colors = listOf(Background, SecondaryBackground)
)

val CardGradient = Brush.verticalGradient(
    colors = listOf(CardBackground, SecondaryBackground)
)

val ShimmerGradient = Brush.horizontalGradient(
    colors = listOf(
        SurfaceVariant.copy(alpha = 0.3f),
        SurfaceVariant.copy(alpha = 0.6f),
        SurfaceVariant.copy(alpha = 0.3f)
    )
)
