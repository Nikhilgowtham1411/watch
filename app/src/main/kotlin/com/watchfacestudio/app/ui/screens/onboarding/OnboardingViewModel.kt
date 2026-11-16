package com.watchfacestudio.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchfacestudio.app.data.local.preferences.AppPreferences
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val appPreferences: AppPreferences
) : ViewModel() {
    fun completeOnboarding(onCompleted: () -> Unit) {
        viewModelScope.launch {
            appPreferences.setFirstLaunchCompleted()
            onCompleted()
        }
    }
}
