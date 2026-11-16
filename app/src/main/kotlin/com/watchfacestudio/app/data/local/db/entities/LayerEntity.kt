package com.watchfacestudio.app.data.local.db.entities

import com.watchfacestudio.app.data.model.LayerType

data class LayerEntity(
    val id: String,
    val designId: String,
    val type: LayerType,
    val posX: Float,
    val posY: Float,
    val width: Int,
    val height: Int,
    val rotation: Float,
    val zIndex: Int,
    val opacity: Float,
    val visible: Boolean,
    val locked: Boolean,
    val data: String
)
