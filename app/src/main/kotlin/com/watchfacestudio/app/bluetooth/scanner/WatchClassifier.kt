package com.watchfacestudio.app.bluetooth.scanner

import com.watchfacestudio.app.bluetooth.model.DiscoveredDevice

/**
 * Decides whether a BLE advertisement plausibly belongs to a smartwatch or
 * fitness band so the scan list is not flooded with earbuds, TVs, beacons
 * and anonymous devices.
 *
 * This only filters what is *listed*. A name match is never treated as proof
 * of the exact model, screen size or watch-face protocol.
 */
object WatchClassifier {

    private const val HEART_RATE_SERVICE = "0000180d"

    private val nameKeywords = listOf(
        "watch", "band", "fit", "wear", "smart", "gear", "ticwatch",
        "noise", "colorfit", "boat", "wave", "lunar", "fire-boltt", "fireboltt", "boltt",
        "cmf", "nothing", "amazfit", "zepp", "mi band", "mi smart", "xiaomi", "redmi", "poco",
        "realme", "oppo", "oneplus", "galaxy", "huawei", "honor", "fitbit", "garmin",
        "fossil", "zeblaze", "amaz", "gtr", "gts", "bip", "pebble"
    )

    private val excludedKeywords = listOf(
        "buds", "airpod", "earbud", "earphone", "headphone", "headset", "speaker",
        "soundbar", "tv", "keyboard", "mouse", "tag", "tracker", "car", "printer", "lamp", "bulb"
    )

    /** Minimum signal strength (dBm) to count as "nearby". */
    const val NEARBY_RSSI = -90

    fun isLikelyWatch(device: DiscoveredDevice): Boolean {
        val name = device.name?.trim()?.lowercase().orEmpty()
        if (name.isBlank()) return false
        if (excludedKeywords.any { containsWord(name, it) }) return false
        if (nameKeywords.any { name.contains(it) }) return true
        // Standard Heart Rate service is advertised by most watches and bands.
        return device.serviceUuids.any { it.uuid.toString().startsWith(HEART_RATE_SERVICE) }
    }

    fun isNearby(device: DiscoveredDevice): Boolean = device.rssi >= NEARBY_RSSI

    private fun containsWord(text: String, word: String): Boolean =
        Regex("(^|[^a-z0-9])${Regex.escape(word)}([^a-z0-9]|$)").containsMatchIn(text)
}
