package com.watchfacestudio.app.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "watchface_preferences")

class AppPreferences(
    private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val KEY_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val KEY_SELECTED_DEVICE_ID = stringPreferencesKey("selected_device_id")
        val KEY_IMAGE_QUALITY = stringPreferencesKey("image_quality") // "HIGH", "MEDIUM", "LOW"
        val KEY_THEME = stringPreferencesKey("app_theme")
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_AUTO_RECONNECT = booleanPreferencesKey("auto_reconnect")
        val KEY_SCAN_TIMEOUT = intPreferencesKey("scan_timeout_seconds")
    }

    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_FIRST_LAUNCH] ?: true
    }

    suspend fun setFirstLaunchCompleted() {
        dataStore.edit { prefs ->
            prefs[KEY_FIRST_LAUNCH] = false
        }
    }

    val selectedDeviceId: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_SELECTED_DEVICE_ID]
    }

    suspend fun setSelectedDeviceId(deviceId: String?) {
        dataStore.edit { prefs ->
            if (deviceId != null) {
                prefs[KEY_SELECTED_DEVICE_ID] = deviceId
            } else {
                prefs.remove(KEY_SELECTED_DEVICE_ID)
            }
        }
    }

    val imageQuality: Flow<String> = dataStore.data.map { prefs ->
        prefs[KEY_IMAGE_QUALITY] ?: "HIGH"
    }

    suspend fun setImageQuality(quality: String) {
        dataStore.edit { prefs ->
            prefs[KEY_IMAGE_QUALITY] = quality
        }
    }

    val autoReconnect: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_AUTO_RECONNECT] ?: true
    }

    suspend fun setAutoReconnect(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_AUTO_RECONNECT] = enabled
        }
    }
}
