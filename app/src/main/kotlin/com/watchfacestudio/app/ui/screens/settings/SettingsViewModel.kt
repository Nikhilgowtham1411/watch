package com.watchfacestudio.app.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchfacestudio.app.data.local.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val context: Context,
    private val appPreferences: AppPreferences
) : ViewModel() {

    val imageQuality: StateFlow<String> = appPreferences.imageQuality.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "HIGH"
    )

    val autoReconnect: StateFlow<Boolean> = appPreferences.autoReconnect.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    fun setImageQuality(quality: String) {
        viewModelScope.launch {
            appPreferences.setImageQuality(quality)
        }
    }

    fun toggleAutoReconnect(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.setAutoReconnect(enabled)
        }
    }

    fun clearCache(onCleared: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    context.cacheDir.deleteRecursively()
                } catch (e: Exception) {
                    // Ignore
                }
            }
            onCleared()
        }
    }
}
