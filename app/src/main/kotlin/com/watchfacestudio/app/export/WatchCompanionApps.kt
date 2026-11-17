package com.watchfacestudio.app.export

import android.content.Context
import android.content.pm.PackageManager
import java.util.UUID

/**
 * Companion apps that can put a custom picture on a watch.
 *
 * Smartwatch watch-face transfer protocols are proprietary and differ per
 * vendor, so the reliable route is to hand the finished image to the watch's
 * own app. Package names are only used after checking they are installed.
 */
object WatchCompanionApps {

    data class Companion(val label: String, val packageName: String)

    const val INSTALL_METHOD_PREFIX = "COMPANION_APP:"

    private val noiseFit = Companion("NoiseFit", "com.gonoise.fit")
    private val boatCrest = Companion("boAt Crest", "com.coveiot.crest")
    private val daFit = Companion("Da Fit", "com.crrepa.band.dafit")
    private val zepp = Companion("Zepp", "com.huami.watch.hmwatchmanager")
    private val miFitness = Companion("Mi Fitness", "com.xiaomi.wearable")
    private val wearOs = Companion("Wear OS", "com.google.android.wearable.app")

    val all: List<Companion> = listOf(noiseFit, boatCrest, daFit, zepp, miFitness, wearOs)

    private val moyoungService = UUID.fromString("0000feea-0000-1000-8000-00805f9b34fb")

    /** Best guess from the watch's name and advertised/discovered services; not proof of anything. */
    fun candidate(deviceName: String?, serviceUuids: Collection<UUID>): Companion? {
        val name = deviceName?.lowercase().orEmpty()
        return when {
            name.contains("noise") || name.contains("colorfit") -> noiseFit
            name.contains("boat") -> boatCrest
            name.contains("amazfit") || name.contains("zepp") -> zepp
            name.contains("mi band") || name.contains("mi smart") || name.contains("redmi watch") -> miFitness
            name.contains("galaxy watch") || name.contains("pixel watch") || name.contains("ticwatch") -> wearOs
            serviceUuids.contains(moyoungService) -> daFit
            else -> null
        }
    }

    fun toInstallMethod(companion: Companion?): String =
        if (companion == null) "MANUAL_EXPORT" else INSTALL_METHOD_PREFIX + companion.packageName

    fun fromInstallMethod(installMethod: String?): Companion? {
        val pkg = installMethod?.takeIf { it.startsWith(INSTALL_METHOD_PREFIX) }
            ?.removePrefix(INSTALL_METHOD_PREFIX) ?: return null
        return all.firstOrNull { it.packageName == pkg }
    }

    fun isInstalled(context: Context, packageName: String): Boolean = try {
        context.packageManager.getPackageInfo(packageName, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }

    /**
     * Picks the companion app to open: the one stored for the watch, then the
     * name-based guess, then the only known companion app that is installed.
     */
    fun resolveInstalled(context: Context, deviceName: String?, installMethod: String?): Companion? {
        val preferred = listOfNotNull(
            fromInstallMethod(installMethod),
            candidate(deviceName, emptyList())
        )
        preferred.firstOrNull { isInstalled(context, it.packageName) }?.let { return it }
        return all.filter { isInstalled(context, it.packageName) }.singleOrNull()
    }
}
