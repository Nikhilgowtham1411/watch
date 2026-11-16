package com.watchfacestudio.app.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Create : Screen("create")
    data object MyDesigns : Screen("my_designs")
    data object Devices : Screen("devices")
    data object Settings : Screen("settings")
    data object ImageCrop : Screen("image_crop/{imageUri}") {
        fun createRoute(imageUri: String): String = "image_crop/${android.net.Uri.encode(imageUri)}"
    }
    data object Editor : Screen("editor?designId={designId}&imageUri={imageUri}") {
        fun createRoute(designId: String? = null, imageUri: String? = null): String {
            val encodedImage = imageUri?.let { android.net.Uri.encode(it) }
            return "editor?designId=${designId.orEmpty()}&imageUri=${encodedImage.orEmpty()}"
        }
    }
    data object ApplyToWatch : Screen("apply/{designId}") {
        fun createRoute(designId: String): String = "apply/$designId"
    }
}
