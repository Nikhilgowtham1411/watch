package com.watchfacestudio.app.navigation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.watchfacestudio.app.ui.components.BottomNavBar
import com.watchfacestudio.app.ui.screens.apply.ApplyToWatchScreen
import com.watchfacestudio.app.ui.screens.create.CreateScreen
import com.watchfacestudio.app.ui.screens.devices.DevicesScreen
import com.watchfacestudio.app.ui.screens.editor.EditorScreen
import com.watchfacestudio.app.ui.screens.home.HomeScreen
import com.watchfacestudio.app.ui.screens.imagecrop.ImageCropScreen
import com.watchfacestudio.app.ui.screens.mydesigns.MyDesignsScreen
import com.watchfacestudio.app.ui.screens.onboarding.OnboardingScreen
import com.watchfacestudio.app.ui.screens.settings.SettingsScreen
import com.watchfacestudio.app.ui.screens.splash.SplashScreen

sealed interface AppRoute {
    data object Splash : AppRoute
    data object Onboarding : AppRoute
    data object Home : AppRoute
    data object Create : AppRoute
    data object MyDesigns : AppRoute
    data object Devices : AppRoute
    data object Settings : AppRoute
    data class ImageCrop(val imageUri: String) : AppRoute
    data class Editor(val imageUri: String? = null, val designId: String? = null) : AppRoute
    data class Apply(val designId: String) : AppRoute
}

@Composable
fun AppNavigation() {
    val backStack = remember { mutableStateListOf<AppRoute>(AppRoute.Splash) }
    val currentRoute = backStack.lastOrNull() ?: AppRoute.Home

    val isTopLevel = currentRoute in listOf(
        AppRoute.Home,
        AppRoute.Create,
        AppRoute.MyDesigns,
        AppRoute.Devices,
        AppRoute.Settings
    )

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
    }

    val bottomNavRouteString = when (currentRoute) {
        AppRoute.Home -> "home"
        AppRoute.Create -> "create"
        AppRoute.MyDesigns -> "my_designs"
        AppRoute.Devices -> "devices"
        AppRoute.Settings -> "settings"
        else -> ""
    }

    Scaffold(
        bottomBar = {
            if (isTopLevel) {
                BottomNavBar(
                    currentRoute = bottomNavRouteString,
                    onNavigate = { route ->
                        val target = when (route) {
                            "home" -> AppRoute.Home
                            "create" -> AppRoute.Create
                            "my_designs" -> AppRoute.MyDesigns
                            "devices" -> AppRoute.Devices
                            "settings" -> AppRoute.Settings
                            else -> AppRoute.Home
                        }
                        if (currentRoute != target) {
                            backStack.clear()
                            backStack.add(target)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentRoute,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "routeAnimation"
            ) { route ->
                when (route) {
                    is AppRoute.Splash -> {
                        SplashScreen(
                            onNavigateToHome = {
                                backStack.clear()
                                backStack.add(AppRoute.Home)
                            },
                            onNavigateToOnboarding = {
                                backStack.clear()
                                backStack.add(AppRoute.Onboarding)
                            }
                        )
                    }

                    is AppRoute.Onboarding -> {
                        OnboardingScreen(
                            onFinish = {
                                backStack.clear()
                                backStack.add(AppRoute.Home)
                            }
                        )
                    }

                    is AppRoute.Home -> {
                        HomeScreen(
                            onCreateClick = { backStack.add(AppRoute.Create) },
                            onDesignClick = { designId -> backStack.add(AppRoute.Editor(designId = designId)) },
                            onDevicesClick = { backStack.add(AppRoute.Devices) },
                            onSettingsClick = { backStack.add(AppRoute.Settings) },
                            onMyDesignsClick = { backStack.add(AppRoute.MyDesigns) }
                        )
                    }

                    is AppRoute.Create -> {
                        CreateScreen(
                            onBackClick = { backStack.removeAt(backStack.lastIndex) },
                            onPhotoSelected = { uri ->
                                backStack.add(AppRoute.ImageCrop(uri.toString()))
                            },
                            onTemplateSelected = { templateId ->
                                backStack.add(AppRoute.Editor(designId = templateId))
                            }
                        )
                    }

                    is AppRoute.MyDesigns -> {
                        MyDesignsScreen(
                            onCreateNewClick = { backStack.add(AppRoute.Create) },
                            onDesignClick = { designId -> backStack.add(AppRoute.Editor(designId = designId)) },
                            onApplyClick = { designId -> backStack.add(AppRoute.Apply(designId)) }
                        )
                    }

                    is AppRoute.Devices -> {
                        DevicesScreen(
                            onBackClick = {
                                if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                                else backStack.add(AppRoute.Home)
                            }
                        )
                    }

                    is AppRoute.Settings -> {
                        SettingsScreen()
                    }

                    is AppRoute.ImageCrop -> {
                        ImageCropScreen(
                            imageUri = route.imageUri,
                            onBackClick = { backStack.removeAt(backStack.lastIndex) },
                            onNextClick = { croppedPath ->
                                backStack.add(AppRoute.Editor(imageUri = croppedPath))
                            }
                        )
                    }

                    is AppRoute.Editor -> {
                        EditorScreen(
                            imageUri = route.imageUri,
                            designId = route.designId,
                            onBackClick = { backStack.removeAt(backStack.lastIndex) },
                            onApplyClick = { id ->
                                backStack.add(AppRoute.Apply(id))
                            }
                        )
                    }

                    is AppRoute.Apply -> {
                        ApplyToWatchScreen(
                            designId = route.designId,
                            onBackClick = { backStack.removeAt(backStack.lastIndex) },
                            onDoneClick = {
                                backStack.clear()
                                backStack.add(AppRoute.Home)
                            },
                            onEditAgainClick = {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        )
                    }
                }
            }
        }
    }
}
