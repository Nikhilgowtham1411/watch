package com.watchfacestudio.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.watchfacestudio.app.WatchFaceStudioApp
import com.watchfacestudio.app.ui.appViewModel
import com.watchfacestudio.app.ui.components.GlassCard
import com.watchfacestudio.app.ui.components.PrimaryButton
import com.watchfacestudio.app.ui.components.SecondaryButton
import com.watchfacestudio.app.ui.theme.Background
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography
import kotlinx.coroutines.launch

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = appViewModel {
        OnboardingViewModel(WatchFaceStudioApp.instance.container.preferences)
    }
) {
    val steps = listOf(
        OnboardingStep(
            title = "Personalized Watch Faces",
            subtitle = "Design Your Style",
            description = "Create bespoke custom watch faces using personal photos, vibrant colors, custom clock fonts and live widgets.",
            icon = Icons.Default.Palette
        ),
        OnboardingStep(
            title = "Bluetooth Discovery",
            subtitle = "Seamless Smartwatch Sync",
            description = "Automatic BLE discovery identifies your smartwatch brand, screen shape and resolution for pixel-perfect preview.",
            icon = Icons.Default.BluetoothSearching
        ),
        OnboardingStep(
            title = "Secure Photo Picker",
            subtitle = "Privacy First",
            description = "Select photos securely using the native Android Photo Picker. No blanket gallery permissions, your photos stay 100% private.",
            icon = Icons.Default.PhotoLibrary
        ),
        OnboardingStep(
            title = "Connect Your Watch",
            subtitle = "Ready to Create",
            description = "Pair with Noise, boAt, Fire-Boltt, Samsung, Amazfit and more to install your watch faces directly.",
            icon = Icons.Default.Watch
        )
    )

    val pagerState = rememberPagerState(pageCount = { steps.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Skip Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (pagerState.currentPage < steps.size - 1) {
                    SecondaryButton(
                        text = "Skip",
                        onClick = {
                            viewModel.completeOnboarding(onFinish)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                val step = steps[page]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Big Icon in Glass Card
                    GlassCard(
                        modifier = Modifier.size(160.dp),
                        shape = CircleShape
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = step.subtitle,
                        style = WatchFaceTypography.labelMedium,
                        color = Primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = step.title,
                        style = WatchFaceTypography.headlineMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = step.description,
                        style = WatchFaceTypography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Dots Indicator
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                repeat(steps.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .height(8.dp)
                            .width(if (isSelected) 24.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Primary else SecondaryBackground)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            if (pagerState.currentPage == steps.size - 1) {
                PrimaryButton(
                    text = "Get Started",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.completeOnboarding(onFinish)
                    }
                )
            } else {
                PrimaryButton(
                    text = "Next",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                )
            }
        }
    }
}
