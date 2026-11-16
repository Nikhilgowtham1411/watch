package com.watchfacestudio.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchfacestudio.app.data.local.db.entities.DesignEntity
import com.watchfacestudio.app.data.local.db.entities.DeviceEntity
import com.watchfacestudio.app.data.model.ScreenShape
import com.watchfacestudio.app.data.repository.DesignRepository
import com.watchfacestudio.app.data.repository.DeviceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val designRepository: DesignRepository,
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    val recentDesigns: StateFlow<List<DesignEntity>> = designRepository.getAllDesigns().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val primaryDevice: StateFlow<DeviceEntity?> = deviceRepository.getPrimaryDevice().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            val device = DeviceEntity(
                id = "default-noise-pro-5",
                bluetoothAddress = "E4:5F:01:2A:3B:4C",
                name = "Noise ColorFit Pro 5",
                brand = "Noise",
                model = "ColorFit Pro 5",
                screenWidth = 410,
                screenHeight = 502,
                screenShape = ScreenShape.RECTANGULAR,
                compatibilityStatus = "Compatible",
                installMethod = "COMPANION_APP",
                lastConnectedAt = System.currentTimeMillis(),
                isPrimary = true
            )
            deviceRepository.saveDevice(device)
        }
    }
}
