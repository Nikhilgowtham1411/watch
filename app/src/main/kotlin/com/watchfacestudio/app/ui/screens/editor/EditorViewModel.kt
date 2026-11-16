package com.watchfacestudio.app.ui.screens.editor

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watchfacestudio.app.data.local.db.entities.ClockSettingsEntity
import com.watchfacestudio.app.data.local.db.entities.DesignEntity
import com.watchfacestudio.app.data.local.db.entities.LayerEntity
import com.watchfacestudio.app.data.local.db.entities.WidgetSettingsEntity
import com.watchfacestudio.app.data.model.ClockType
import com.watchfacestudio.app.data.model.LayerType
import com.watchfacestudio.app.data.model.ScreenShape
import com.watchfacestudio.app.data.model.TimeFormat
import com.watchfacestudio.app.data.model.WidgetType
import com.watchfacestudio.app.data.repository.DesignRepository
import com.watchfacestudio.app.data.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

enum class ClockPosition(val label: String, val xWeight: Float, val yWeight: Float) {
    TOP_LEFT("Top Left", 0.15f, 0.20f),
    TOP_CENTER("Top Center", 0.5f, 0.20f),
    TOP_RIGHT("Top Right", 0.85f, 0.20f),
    CENTER_LEFT("Center Left", 0.15f, 0.50f),
    CENTER("Center", 0.5f, 0.50f),
    CENTER_RIGHT("Center Right", 0.85f, 0.50f),
    BOTTOM_LEFT("Bottom Left", 0.15f, 0.80f),
    BOTTOM_CENTER("Bottom Center", 0.5f, 0.80f),
    BOTTOM_RIGHT("Bottom Right", 0.85f, 0.80f)
}

data class UiLayer(
    val id: String,
    val name: String,
    val type: LayerType,
    val visible: Boolean = true,
    val locked: Boolean = false,
    val zIndex: Int
)

data class UiWidget(
    val type: WidgetType,
    val name: String,
    val previewValue: String,
    val enabled: Boolean
)

data class EditorState(
    val designId: String = UUID.randomUUID().toString(),
    val designName: String = "My Watch Face",
    val imagePath: String? = null,
    val shape: ScreenShape = ScreenShape.RECTANGULAR,
    // Clock
    val clockPosition: ClockPosition = ClockPosition.CENTER,
    val clockSizePercent: Int = 120,
    val clockFont: String = "Modern",
    val clockColor: Color = Color.White,
    val clockOpacity: Float = 1.0f,
    val isAnalog: Boolean = false,
    val is24Hour: Boolean = false,
    val showSeconds: Boolean = false,
    // Date
    val dateVisible: Boolean = true,
    val dateColor: Color = Color(0xFFA78BFA),
    // Background adjustments
    val bgBlur: Float = 0f,
    val bgBrightness: Float = 1.0f,
    // Layers
    val layers: List<UiLayer> = listOf(
        UiLayer("l1", "Time", LayerType.DIGITAL_TIME, true, false, 3),
        UiLayer("l2", "Date", LayerType.DATE, true, false, 2),
        UiLayer("l3", "Foreground (PNG)", LayerType.FOREGROUND_SUBJECT, true, false, 1),
        UiLayer("l4", "Background (Image)", LayerType.BACKGROUND_IMAGE, true, true, 0)
    ),
    // Widgets
    val widgets: List<UiWidget> = listOf(
        UiWidget(WidgetType.DATE, "Date", "Mon, Nov 16", true),
        UiWidget(WidgetType.STEPS, "Steps", "8,420", false),
        UiWidget(WidgetType.HEARTRATE, "Heart Rate", "72 bpm", false),
        UiWidget(WidgetType.BATTERY, "Battery", "78%", true),
        UiWidget(WidgetType.DAY, "Day", "Monday", false),
        UiWidget(WidgetType.WEATHER, "Weather", "28°C", false),
        UiWidget(WidgetType.CALORIES, "Calories", "312 kcal", false),
        UiWidget(WidgetType.DISTANCE, "Distance", "5.2 km", false)
    )
)

class EditorViewModel(
    private val designRepository: DesignRepository,
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state.asStateFlow()

    private val _selectedTab = MutableStateFlow("Time")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private val undoStack = mutableListOf<EditorState>()
    private val redoStack = mutableListOf<EditorState>()

    init {
        viewModelScope.launch {
            val primary = deviceRepository.getPrimaryDevice().firstOrNull()
            if (primary != null) {
                _state.value = _state.value.copy(shape = primary.screenShape)
            }
        }
    }

    fun initFromImage(imagePath: String) {
        saveUndoSnapshot()
        _state.value = _state.value.copy(imagePath = imagePath)
    }

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    private fun saveUndoSnapshot() {
        if (undoStack.size > 25) undoStack.removeAt(0)
        undoStack.add(_state.value)
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.add(_state.value)
            _state.value = undoStack.removeAt(undoStack.lastIndex)
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.add(_state.value)
            _state.value = redoStack.removeAt(redoStack.lastIndex)
        }
    }

    fun setClockPosition(pos: ClockPosition) {
        saveUndoSnapshot()
        _state.value = _state.value.copy(clockPosition = pos)
    }

    fun setClockSize(percent: Int) {
        _state.value = _state.value.copy(clockSizePercent = percent)
    }

    fun setClockFont(font: String) {
        saveUndoSnapshot()
        _state.value = _state.value.copy(clockFont = font)
    }

    fun setClockColor(color: Color) {
        saveUndoSnapshot()
        _state.value = _state.value.copy(clockColor = color)
    }

    fun setClockOpacity(opacity: Float) {
        _state.value = _state.value.copy(clockOpacity = opacity)
    }

    fun toggleAnalog() {
        saveUndoSnapshot()
        _state.value = _state.value.copy(isAnalog = !_state.value.isAnalog)
    }

    fun toggleWidget(type: WidgetType) {
        saveUndoSnapshot()
        val updated = _state.value.widgets.map {
            if (it.type == type) it.copy(enabled = !it.enabled) else it
        }
        _state.value = _state.value.copy(widgets = updated)
    }

    fun toggleLayerVisibility(id: String) {
        saveUndoSnapshot()
        val updated = _state.value.layers.map {
            if (it.id == id) it.copy(visible = !it.visible) else it
        }
        _state.value = _state.value.copy(layers = updated)
    }

    fun moveLayerForward(id: String) {
        saveUndoSnapshot()
        val list = _state.value.layers.toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index > 0) {
            val item = list.removeAt(index)
            list.add(index - 1, item)
            _state.value = _state.value.copy(layers = list)
        }
    }

    fun moveLayerBackward(id: String) {
        saveUndoSnapshot()
        val list = _state.value.layers.toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index >= 0 && index < list.size - 1) {
            val item = list.removeAt(index)
            list.add(index + 1, item)
            _state.value = _state.value.copy(layers = list)
        }
    }

    fun saveDesign(onSaved: () -> Unit) {
        viewModelScope.launch {
            val currentState = _state.value
            val now = System.currentTimeMillis()
            val design = DesignEntity(
                id = currentState.designId,
                name = currentState.designName,
                imagePath = currentState.imagePath,
                optimizedImagePath = currentState.imagePath,
                canvasWidth = 410,
                canvasHeight = 502,
                canvasShape = currentState.shape,
                targetDeviceId = null,
                createdAt = now,
                modifiedAt = now
            )

            val clockSettings = ClockSettingsEntity(
                id = UUID.randomUUID().toString(),
                designId = currentState.designId,
                clockType = if (currentState.isAnalog) ClockType.ANALOG else ClockType.DIGITAL,
                fontFamily = currentState.clockFont,
                fontSize = currentState.clockSizePercent,
                fontWeight = "Bold",
                textColor = currentState.clockColor.value.toLong(),
                posX = currentState.clockPosition.xWeight,
                posY = currentState.clockPosition.yWeight,
                timeFormat = if (currentState.is24Hour) TimeFormat.H24 else TimeFormat.H12,
                showSeconds = currentState.showSeconds,
                showAmPm = true,
                leadingZero = false,
                shadowEnabled = true,
                glowEnabled = false,
                outlineEnabled = false,
                opacity = currentState.clockOpacity,
                rotation = 0f,
                backgroundColor = null,
                backgroundRadius = 0
            )

            val layers = currentState.layers.mapIndexed { idx, layer ->
                LayerEntity(
                    id = layer.id,
                    designId = currentState.designId,
                    type = layer.type,
                    posX = 0f,
                    posY = 0f,
                    width = 410,
                    height = 502,
                    rotation = 0f,
                    zIndex = idx,
                    opacity = 1f,
                    visible = layer.visible,
                    locked = layer.locked,
                    data = "{}"
                )
            }

            val widgets = currentState.widgets.filter { it.enabled }.map { w ->
                WidgetSettingsEntity(
                    id = UUID.randomUUID().toString(),
                    designId = currentState.designId,
                    widgetType = w.type,
                    posX = 0.5f,
                    posY = 0.85f,
                    width = 100,
                    height = 30,
                    textColor = 0xFFFFFFFF,
                    fontSize = 12,
                    fontFamily = "Regular",
                    alignment = "CENTER",
                    visible = true,
                    opacity = 1f
                )
            }

            designRepository.saveDesign(design, layers, clockSettings, widgets)
            onSaved()
        }
    }
}
