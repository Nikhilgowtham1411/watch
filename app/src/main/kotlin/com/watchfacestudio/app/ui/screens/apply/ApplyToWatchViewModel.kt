package com.watchfacestudio.app.ui.screens.apply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchfacestudio.app.data.local.db.entities.DesignEntity
import com.watchfacestudio.app.data.local.db.entities.DeviceEntity
import com.watchfacestudio.app.data.repository.DesignRepository
import com.watchfacestudio.app.data.repository.DeviceRepository
import com.watchfacestudio.app.export.InstallResult
import com.watchfacestudio.app.export.WatchFaceInstaller
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ApplyState {
    data class InProgress(val progress: Float, val statusMessage: String) : ApplyState()
    data class Completed(val message: String, val watchName: String) : ApplyState()
    data class ReadyToSend(
        val imageUri: android.net.Uri,
        val width: Int,
        val height: Int,
        val companion: com.watchfacestudio.app.export.WatchCompanionApps.Companion?,
        val watchName: String
    ) : ApplyState()
    data class Failed(val reason: String) : ApplyState()
}

class ApplyToWatchViewModel(
    private val designRepository: DesignRepository,
    private val deviceRepository: DeviceRepository,
    private val installer: WatchFaceInstaller
) : ViewModel() {

    private val _applyState = MutableStateFlow<ApplyState>(ApplyState.InProgress(0.1f, "Initializing..."))
    val applyState: StateFlow<ApplyState> = _applyState.asStateFlow()

    private val _currentDesign = MutableStateFlow<DesignEntity?>(null)
    val currentDesign: StateFlow<DesignEntity?> = _currentDesign.asStateFlow()

    val selectedDevice: StateFlow<DeviceEntity?> = deviceRepository.getPrimaryDevice().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun startSync(designId: String) {
        viewModelScope.launch {
            val design = designRepository.getDesignById(designId)
            _currentDesign.value = design

            if (design == null) {
                _applyState.value = ApplyState.Failed("Design not found")
                return@launch
            }

            val device = deviceRepository.getPrimaryDevice().firstOrNull()

            val result = installer.applyWatchFace(
                design = design,
                device = device,
                onProgress = { progress, msg ->
                    _applyState.value = ApplyState.InProgress(progress, msg)
                }
            )

            when (result) {
                is InstallResult.Success -> {
                    _applyState.value = ApplyState.Completed(result.message, result.watchName)
                }
                is InstallResult.ReadyToSend -> {
                    _applyState.value = ApplyState.ReadyToSend(
                        imageUri = result.imageUri,
                        width = result.width,
                        height = result.height,
                        companion = result.companion,
                        watchName = result.watchName
                    )
                }
                is InstallResult.Error -> {
                    _applyState.value = ApplyState.Failed(result.reason)
                }
            }
        }
    }
}
