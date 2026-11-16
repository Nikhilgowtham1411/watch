package com.watchfacestudio.app.ui.screens.splash

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.WatchFaceStudioApp
import com.watchfacestudio.app.ui.appViewModel
import com.watchfacestudio.app.ui.theme.Background
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.PrimaryGradient
import com.watchfacestudio.app.ui.theme.SecondaryAccent
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    viewModel: SplashViewModel = appViewModel {
        SplashViewModel(WatchFaceStudioApp.instance.container.preferences)
    }
) {
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState()
    var progress by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1800, easing = LinearEasing),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        progress = 1f
        delay(2000)
        if (isFirstLaunch) {
            onNavigateToOnboarding()
        } else {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Watch Icon with Glowing Orbital Ring
            Box(
                modifier = Modifier
                    .size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // Orbital glow ring
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                listOf(Color.Transparent, Primary, SecondaryAccent, Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                // Watch Bezel
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = Primary)
                        .clip(RoundedCornerShape(32.dp))
                        .background(SecondaryBackground)
                        .border(2.dp, Primary.copy(alpha = 0.5f), RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Watch,
                        contentDescription = "WatchFace Studio",
                        tint = Primary,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // App Title
            Text(
                text = "WatchFace",
                style = WatchFaceTypography.headlineLarge,
                color = TextPrimary
            )
            Text(
                text = "Studio",
                style = WatchFaceTypography.headlineLarge,
                color = SecondaryAccent
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Create • Customize • Sync",
                style = WatchFaceTypography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Animated Loading Bar
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF222233))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(4.dp)
                        .background(PrimaryGradient)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Loading...",
                style = WatchFaceTypography.labelSmall,
                color = TextSecondary.copy(alpha = 0.6f)
            )
        }
    }
}
