package com.watchfacestudio.app.bluetooth.device

import com.watchfacestudio.app.bluetooth.model.ConfidenceLevel
import com.watchfacestudio.app.bluetooth.model.DeviceIdentification
import com.watchfacestudio.app.bluetooth.model.InstallMethod
import com.watchfacestudio.app.data.model.ScreenShape

data class KnownWatchProfile(
    val namePatterns: List<Regex>,
    val brand: String,
    val model: String,
    val screenWidth: Int,
    val screenHeight: Int,
    val screenShape: ScreenShape,
    val supportsCustomWatchFace: Boolean = true,
    val installMethod: InstallMethod = InstallMethod.COMPANION_APP,
    val companionAppPackage: String? = null
)

object DeviceDatabase {
    val profiles = listOf(
        // Noise
        KnownWatchProfile(
            namePatterns = listOf(Regex("(?i).*colorfit.*pro.*5.*"), Regex("(?i).*noise.*pro.*5.*")),
            brand = "Noise",
            model = "ColorFit Pro 5",
            screenWidth = 410,
            screenHeight = 502,
            screenShape = ScreenShape.RECTANGULAR,
            installMethod = InstallMethod.COMPANION_APP,
            companionAppPackage = "com.gonoise.fit"
        ),
        KnownWatchProfile(
            namePatterns = listOf(Regex("(?i).*colorfit.*ultra.*3.*"), Regex("(?i).*noise.*ultra.*3.*")),
            brand = "Noise",
            model = "ColorFit Ultra 3",
            screenWidth = 410,
            screenHeight = 502,
            screenShape = ScreenShape.RECTANGULAR,
            installMethod = InstallMethod.COMPANION_APP,
            companionAppPackage = "com.gonoise.fit"
        ),
        KnownWatchProfile(
            namePatterns = listOf(Regex("(?i).*noise.*pulse.*"), Regex("(?i).*colorfit.*pulse.*")),
            brand = "Noise",
            model = "ColorFit Pulse",
            screenWidth = 240,
            screenHeight = 280,
            screenShape = ScreenShape.RECTANGULAR,
            installMethod = InstallMethod.COMPANION_APP,
            companionAppPackage = "com.gonoise.fit"
        ),
        KnownWatchProfile(
            namePatterns = listOf(Regex("(?i).*noise.*")),
            brand = "Noise",
            model = "Smartwatch",
            screenWidth = 360,
            screenHeight = 360,
            screenShape = ScreenShape.CIRCULAR,
            installMethod = InstallMethod.COMPANION_APP,
            companionAppPackage = "com.gonoise.fit"
        ),

        // boAt
        KnownWatchProfile(
            namePatterns = listOf(Regex("(?i).*wave.*sigma.*"), Regex("(?i).*boat.*sigma.*")),
            brand = "boAt",
            model = "Wave Sigma",
            screenWidth = 368,
            screenHeight = 448,
            screenShape = ScreenShape.RECTANGULAR,
            installMethod = InstallMethod.COMPANION_APP,
            companionAppPackage = "com.coveiot.crest"
        ),
        KnownWatchProfile(
            namePatterns = listOf(Regex("(?i).*boat.*storm.*"), Regex("(?i).*wave.*storm.*")),
            brand = "boAt",
            model = "Storm Call",
            screenWidth = 240,
            screenHeight = 284,
            screenShape = ScreenShape.RECTANGULAR,
            installMethod = InstallMethod.COMPANION_APP,
            companionAppPackage = "com.coveiot.crest"
        ),
        KnownWatchProfile(
            namePatterns = listOf(Regex("(?i).*boat.*")),
            brand = "boAt",
            model = "Wave Watch",
            screenWidth = 240,
            screenHeight = 280,
            screenShape = ScreenShape.RECTANGULAR,
            installMethod = InstallMethod.COMPANION_APP,
            companionAppPackage = "com.coveiot.crest"
        ),

        // Fire-Boltt
        KnownWatchProfile(
            namePatterns = listOf(Regex("(?i).*fire.*boltt.*phoenix.*"), Regex("(?i).*phoenix.*")),
            brand = "Fire-Boltt",
            model = "Phoenix",
            screenWidth = 240,
            screenHeight = 240,
            screenShape = ScreenShape.CIRCULAR,
            installMethod = InstallMethod.COMPANION_APP,
            companionAppPackage = "com.crrepa.band.dafit"
        ),
        KnownWatchProfile(
            namePatterns = listOf(Regex("(?i).*fire.*boltt.*ring.*"), Regex("(?i).*ring.*3.*")),
            brand = "Fire-Boltt",
            model = "Ring 3",
            screenWidth = 240,
            screenHeight = 280,
            screenShape = ScreenShape.RECTANGULAR,
            installMethod = InstallMethod.COMPANION_APP,
            companionAppPackage = "com.crrepa.band.dafit"
        ),
        KnownWatchProfile(
            namePatterns = listOf(Regex("(?i).*fire.*boltt.*"), Regex("(?i).*boltt.*")),
            brand = "Fire-Boltt",
            model = "Smartwatch",
            screenWidth = 240,
            screenHeight = 240,
            screenShape = ScreenShape.CIRCULAR,
            installMethod = InstallMethod.COMPANION_APP,
            companionAppPackage = "com.crrepa.band.dafit"
        ),

        // Samsung Galaxy Watch (Wear OS)
        KnownWatchProfile(
            namePatterns = listOf(Regex("(?i).*galaxy.*watch.*6.*"), Regex("(?i).*galaxy.*watch.*5.*"), Regex("(?i).*galaxy.*watch.*4.*")),
            brand = "Samsung",
            model = "Galaxy Watch",
            screenWidth = 450,
            screenHeight = 450,
            screenShape = ScreenShape.CIRCULAR,
            installMethod = InstallMethod.WEAR_OS,
            companionAppPackage = "com.samsung.android.waterplugin"
        ),

        // Amazfit
        KnownWatchProfile(
            namePatterns = listOf(Regex("(?i).*amazfit.*gtr.*"), Regex("(?i).*amazfit.*gts.*")),
            brand = "Amazfit",
            model = "GTR / GTS",
            screenWidth = 390,
            screenHeight = 450,
            screenShape = ScreenShape.RECTANGULAR,
            installMethod = InstallMethod.COMPANION_APP,
            companionAppPackage = "com.huami.watch.hmwatchmanager"
        )
    )

    fun identify(deviceName: String?): DeviceIdentification {
        if (deviceName.isNullOrBlank()) {
            return DeviceIdentification(
                brand = "Generic",
                model = "Smartwatch",
                screenWidth = 360,
                screenHeight = 360,
                screenShape = ScreenShape.CIRCULAR,
                supportsCustomWatchFace = true,
                installMethod = InstallMethod.MANUAL_EXPORT,
                companionAppPackage = null,
                confidence = ConfidenceLevel.LOW
            )
        }

        for (profile in profiles) {
            if (profile.namePatterns.any { it.matches(deviceName) }) {
                return DeviceIdentification(
                    brand = profile.brand,
                    model = profile.model,
                    screenWidth = profile.screenWidth,
                    screenHeight = profile.screenHeight,
                    screenShape = profile.screenShape,
                    supportsCustomWatchFace = profile.supportsCustomWatchFace,
                    installMethod = profile.installMethod,
                    companionAppPackage = profile.companionAppPackage,
                    confidence = ConfidenceLevel.HIGH
                )
            }
        }

        // Generic smartwatch fallback
        return DeviceIdentification(
            brand = "Smartwatch",
            model = deviceName,
            screenWidth = 360,
            screenHeight = 360,
            screenShape = ScreenShape.CIRCULAR,
            supportsCustomWatchFace = true,
            installMethod = InstallMethod.MANUAL_EXPORT,
            companionAppPackage = null,
            confidence = ConfidenceLevel.MEDIUM
        )
    }
}
