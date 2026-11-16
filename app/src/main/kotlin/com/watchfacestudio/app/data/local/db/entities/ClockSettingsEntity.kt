package com.watchfacestudio.app.data.local.db.entities

import com.watchfacestudio.app.data.model.ClockType
import com.watchfacestudio.app.data.model.TimeFormat

data class ClockSettingsEntity(
    val id: String,
    val designId: String,
    val clockType: ClockType,
    val fontFamily: String,
    val fontSize: Int,
    val fontWeight: String,
    val textColor: Long,
    val posX: Float,
    val posY: Float,
    val timeFormat: TimeFormat,
    val showSeconds: Boolean,
    val showAmPm: Boolean,
    val leadingZero: Boolean,
    val shadowEnabled: Boolean,
    val glowEnabled: Boolean,
    val outlineEnabled: Boolean,
    val opacity: Float,
    val rotation: Float,
    val backgroundColor: Long?,
    val backgroundRadius: Int
)
