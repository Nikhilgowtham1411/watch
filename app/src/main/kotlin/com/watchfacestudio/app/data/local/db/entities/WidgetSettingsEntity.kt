package com.watchfacestudio.app.data.local.db.entities

import com.watchfacestudio.app.data.model.WidgetType

data class WidgetSettingsEntity(
    val id: String,
    val designId: String,
    val widgetType: WidgetType,
    val posX: Float,
    val posY: Float,
    val width: Int,
    val height: Int,
    val textColor: Long,
    val fontSize: Int,
    val fontFamily: String,
    val alignment: String,
    val visible: Boolean,
    val opacity: Float
)
