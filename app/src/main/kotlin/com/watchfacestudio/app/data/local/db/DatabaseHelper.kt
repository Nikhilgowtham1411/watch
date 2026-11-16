package com.watchfacestudio.app.data.local.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.watchfacestudio.app.data.local.db.dao.ClockSettingsDao
import com.watchfacestudio.app.data.local.db.dao.DesignDao
import com.watchfacestudio.app.data.local.db.dao.DeviceDao
import com.watchfacestudio.app.data.local.db.dao.LayerDao
import com.watchfacestudio.app.data.local.db.dao.WidgetSettingsDao
import com.watchfacestudio.app.data.local.db.entities.ClockSettingsEntity
import com.watchfacestudio.app.data.local.db.entities.DesignEntity
import com.watchfacestudio.app.data.local.db.entities.DeviceEntity
import com.watchfacestudio.app.data.local.db.entities.LayerEntity
import com.watchfacestudio.app.data.local.db.entities.WidgetSettingsEntity
import com.watchfacestudio.app.data.model.ClockType
import com.watchfacestudio.app.data.model.LayerType
import com.watchfacestudio.app.data.model.ScreenShape
import com.watchfacestudio.app.data.model.TimeFormat
import com.watchfacestudio.app.data.model.WidgetType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "watchface_studio.db", null, 1) {

    private val designsTrigger = MutableStateFlow(System.currentTimeMillis())
    private val devicesTrigger = MutableStateFlow(System.currentTimeMillis())
    private val layersTrigger = MutableStateFlow(System.currentTimeMillis())
    private val widgetsTrigger = MutableStateFlow(System.currentTimeMillis())

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS designs (
                id TEXT PRIMARY KEY,
                name TEXT,
                imagePath TEXT,
                optimizedImagePath TEXT,
                canvasWidth INTEGER,
                canvasHeight INTEGER,
                canvasShape TEXT,
                targetDeviceId TEXT,
                createdAt INTEGER,
                modifiedAt INTEGER
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS devices (
                id TEXT PRIMARY KEY,
                bluetoothAddress TEXT,
                name TEXT,
                brand TEXT,
                model TEXT,
                screenWidth INTEGER,
                screenHeight INTEGER,
                screenShape TEXT,
                compatibilityStatus TEXT,
                installMethod TEXT,
                lastConnectedAt INTEGER,
                isPrimary INTEGER
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS layers (
                id TEXT PRIMARY KEY,
                designId TEXT,
                type TEXT,
                posX REAL,
                posY REAL,
                width INTEGER,
                height INTEGER,
                rotation REAL,
                zIndex INTEGER,
                opacity REAL,
                visible INTEGER,
                locked INTEGER,
                data TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS clock_settings (
                id TEXT PRIMARY KEY,
                designId TEXT,
                clockType TEXT,
                fontFamily TEXT,
                fontSize INTEGER,
                fontWeight TEXT,
                textColor INTEGER,
                posX REAL,
                posY REAL,
                timeFormat TEXT,
                showSeconds INTEGER,
                showAmPm INTEGER,
                leadingZero INTEGER,
                shadowEnabled INTEGER,
                glowEnabled INTEGER,
                outlineEnabled INTEGER,
                opacity REAL,
                rotation REAL,
                backgroundColor INTEGER,
                backgroundRadius INTEGER
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS widget_settings (
                id TEXT PRIMARY KEY,
                designId TEXT,
                widgetType TEXT,
                posX REAL,
                posY REAL,
                width INTEGER,
                height INTEGER,
                textColor INTEGER,
                fontSize INTEGER,
                fontFamily TEXT,
                alignment TEXT,
                visible INTEGER,
                opacity REAL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS designs")
        db.execSQL("DROP TABLE IF EXISTS devices")
        db.execSQL("DROP TABLE IF EXISTS layers")
        db.execSQL("DROP TABLE IF EXISTS clock_settings")
        db.execSQL("DROP TABLE IF EXISTS widget_settings")
        onCreate(db)
    }

    val designDao = object : DesignDao {
        override suspend fun insert(design: DesignEntity) {
            val cv = ContentValues().apply {
                put("id", design.id)
                put("name", design.name)
                put("imagePath", design.imagePath)
                put("optimizedImagePath", design.optimizedImagePath)
                put("canvasWidth", design.canvasWidth)
                put("canvasHeight", design.canvasHeight)
                put("canvasShape", design.canvasShape.name)
                put("targetDeviceId", design.targetDeviceId)
                put("createdAt", design.createdAt)
                put("modifiedAt", design.modifiedAt)
            }
            writableDatabase.insertWithOnConflict("designs", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            designsTrigger.value = System.currentTimeMillis()
        }

        override suspend fun update(design: DesignEntity) {
            insert(design)
        }

        override suspend fun delete(design: DesignEntity) {
            deleteById(design.id)
        }

        override suspend fun deleteById(id: String) {
            writableDatabase.delete("designs", "id = ?", arrayOf(id))
            designsTrigger.value = System.currentTimeMillis()
        }

        override fun getAll(): Flow<List<DesignEntity>> {
            return designsTrigger.map {
                val list = mutableListOf<DesignEntity>()
                val cursor = readableDatabase.query("designs", null, null, null, null, null, "modifiedAt DESC")
                while (cursor.moveToNext()) {
                    list.add(cursorToDesign(cursor))
                }
                cursor.close()
                list
            }
        }

        override suspend fun getById(id: String): DesignEntity? {
            val cursor = readableDatabase.query("designs", null, "id = ?", arrayOf(id), null, null, null)
            val result = if (cursor.moveToFirst()) cursorToDesign(cursor) else null
            cursor.close()
            return result
        }

        override fun searchByName(query: String): Flow<List<DesignEntity>> {
            return designsTrigger.map {
                val list = mutableListOf<DesignEntity>()
                val cursor = readableDatabase.query(
                    "designs", null, "name LIKE ?", arrayOf("%$query%"), null, null, "modifiedAt DESC"
                )
                while (cursor.moveToNext()) {
                    list.add(cursorToDesign(cursor))
                }
                cursor.close()
                list
            }
        }
    }

    val deviceDao = object : DeviceDao {
        override suspend fun insert(device: DeviceEntity) {
            val cv = ContentValues().apply {
                put("id", device.id)
                put("bluetoothAddress", device.bluetoothAddress)
                put("name", device.name)
                put("brand", device.brand)
                put("model", device.model)
                put("screenWidth", device.screenWidth)
                put("screenHeight", device.screenHeight)
                put("screenShape", device.screenShape.name)
                put("compatibilityStatus", device.compatibilityStatus)
                put("installMethod", device.installMethod)
                put("lastConnectedAt", device.lastConnectedAt)
                put("isPrimary", if (device.isPrimary) 1 else 0)
            }
            writableDatabase.insertWithOnConflict("devices", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            devicesTrigger.value = System.currentTimeMillis()
        }

        override suspend fun update(device: DeviceEntity) {
            insert(device)
        }

        override suspend fun delete(device: DeviceEntity) {
            deleteById(device.id)
        }

        override suspend fun deleteById(id: String) {
            writableDatabase.delete("devices", "id = ?", arrayOf(id))
            devicesTrigger.value = System.currentTimeMillis()
        }

        override suspend fun getById(id: String): DeviceEntity? {
            val cursor = readableDatabase.query("devices", null, "id = ?", arrayOf(id), null, null, null)
            val result = if (cursor.moveToFirst()) cursorToDevice(cursor) else null
            cursor.close()
            return result
        }

        override fun getPrimaryDevice(): Flow<DeviceEntity?> {
            return devicesTrigger.map {
                val cursor = readableDatabase.query("devices", null, "isPrimary = 1", null, null, null, "lastConnectedAt DESC LIMIT 1")
                val result = if (cursor.moveToFirst()) cursorToDevice(cursor) else null
                cursor.close()
                result
            }
        }

        override fun getAllDevices(): Flow<List<DeviceEntity>> {
            return devicesTrigger.map {
                val list = mutableListOf<DeviceEntity>()
                val cursor = readableDatabase.query("devices", null, null, null, null, null, "isPrimary DESC, name ASC")
                while (cursor.moveToNext()) {
                    list.add(cursorToDevice(cursor))
                }
                cursor.close()
                list
            }
        }

        override suspend fun updateConnectionStatus(id: String, timestamp: Long?) {
            val cv = ContentValues().apply {
                put("lastConnectedAt", timestamp)
            }
            writableDatabase.update("devices", cv, "id = ?", arrayOf(id))
            devicesTrigger.value = System.currentTimeMillis()
        }
    }

    val layerDao = object : LayerDao {
        override suspend fun insert(layer: LayerEntity) {
            insertAll(listOf(layer))
        }

        override suspend fun insertAll(layers: List<LayerEntity>) {
            val db = writableDatabase
            db.beginTransaction()
            try {
                for (layer in layers) {
                    val cv = ContentValues().apply {
                        put("id", layer.id)
                        put("designId", layer.designId)
                        put("type", layer.type.name)
                        put("posX", layer.posX)
                        put("posY", layer.posY)
                        put("width", layer.width)
                        put("height", layer.height)
                        put("rotation", layer.rotation)
                        put("zIndex", layer.zIndex)
                        put("opacity", layer.opacity)
                        put("visible", if (layer.visible) 1 else 0)
                        put("locked", if (layer.locked) 1 else 0)
                        put("data", layer.data)
                    }
                    db.insertWithOnConflict("layers", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
            layersTrigger.value = System.currentTimeMillis()
        }

        override suspend fun update(layer: LayerEntity) {
            insert(layer)
        }

        override suspend fun delete(layer: LayerEntity) {
            deleteById(layer.id)
        }

        override suspend fun deleteById(id: String) {
            writableDatabase.delete("layers", "id = ?", arrayOf(id))
            layersTrigger.value = System.currentTimeMillis()
        }

        override suspend fun deleteByDesignId(designId: String) {
            writableDatabase.delete("layers", "designId = ?", arrayOf(designId))
            layersTrigger.value = System.currentTimeMillis()
        }

        override fun getByDesignId(designId: String): Flow<List<LayerEntity>> {
            return layersTrigger.map {
                val list = mutableListOf<LayerEntity>()
                val cursor = readableDatabase.query("layers", null, "designId = ?", arrayOf(designId), null, null, "zIndex ASC")
                while (cursor.moveToNext()) {
                    list.add(cursorToLayer(cursor))
                }
                cursor.close()
                list
            }
        }

        override suspend fun getById(id: String): LayerEntity? {
            val cursor = readableDatabase.query("layers", null, "id = ?", arrayOf(id), null, null, null)
            val result = if (cursor.moveToFirst()) cursorToLayer(cursor) else null
            cursor.close()
            return result
        }

        override suspend fun updateZIndex(id: String, zIndex: Int) {
            val cv = ContentValues().apply { put("zIndex", zIndex) }
            writableDatabase.update("layers", cv, "id = ?", arrayOf(id))
            layersTrigger.value = System.currentTimeMillis()
        }

        override suspend fun updateVisibility(id: String, visible: Boolean) {
            val cv = ContentValues().apply { put("visible", if (visible) 1 else 0) }
            writableDatabase.update("layers", cv, "id = ?", arrayOf(id))
            layersTrigger.value = System.currentTimeMillis()
        }
    }

    val clockSettingsDao = object : ClockSettingsDao {
        override suspend fun insert(clockSettings: ClockSettingsEntity) {
            val cv = ContentValues().apply {
                put("id", clockSettings.id)
                put("designId", clockSettings.designId)
                put("clockType", clockSettings.clockType.name)
                put("fontFamily", clockSettings.fontFamily)
                put("fontSize", clockSettings.fontSize)
                put("fontWeight", clockSettings.fontWeight)
                put("textColor", clockSettings.textColor)
                put("posX", clockSettings.posX)
                put("posY", clockSettings.posY)
                put("timeFormat", clockSettings.timeFormat.name)
                put("showSeconds", if (clockSettings.showSeconds) 1 else 0)
                put("showAmPm", if (clockSettings.showAmPm) 1 else 0)
                put("leadingZero", if (clockSettings.leadingZero) 1 else 0)
                put("shadowEnabled", if (clockSettings.shadowEnabled) 1 else 0)
                put("glowEnabled", if (clockSettings.glowEnabled) 1 else 0)
                put("outlineEnabled", if (clockSettings.outlineEnabled) 1 else 0)
                put("opacity", clockSettings.opacity)
                put("rotation", clockSettings.rotation)
                put("backgroundColor", clockSettings.backgroundColor)
                put("backgroundRadius", clockSettings.backgroundRadius)
            }
            writableDatabase.insertWithOnConflict("clock_settings", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        }

        override suspend fun update(clockSettings: ClockSettingsEntity) {
            insert(clockSettings)
        }

        override suspend fun delete(clockSettings: ClockSettingsEntity) {
            deleteById(clockSettings.id)
        }

        override suspend fun deleteById(id: String) {
            writableDatabase.delete("clock_settings", "id = ?", arrayOf(id))
        }

        override suspend fun getByDesignId(designId: String): ClockSettingsEntity? {
            val cursor = readableDatabase.query("clock_settings", null, "designId = ?", arrayOf(designId), null, null, null)
            val result = if (cursor.moveToFirst()) cursorToClock(cursor) else null
            cursor.close()
            return result
        }

        override suspend fun getById(id: String): ClockSettingsEntity? {
            val cursor = readableDatabase.query("clock_settings", null, "id = ?", arrayOf(id), null, null, null)
            val result = if (cursor.moveToFirst()) cursorToClock(cursor) else null
            cursor.close()
            return result
        }
    }

    val widgetSettingsDao = object : WidgetSettingsDao {
        override suspend fun insert(widgetSettings: WidgetSettingsEntity) {
            insertAll(listOf(widgetSettings))
        }

        override suspend fun insertAll(widgetSettings: List<WidgetSettingsEntity>) {
            val db = writableDatabase
            db.beginTransaction()
            try {
                for (w in widgetSettings) {
                    val cv = ContentValues().apply {
                        put("id", w.id)
                        put("designId", w.designId)
                        put("widgetType", w.widgetType.name)
                        put("posX", w.posX)
                        put("posY", w.posY)
                        put("width", w.width)
                        put("height", w.height)
                        put("textColor", w.textColor)
                        put("fontSize", w.fontSize)
                        put("fontFamily", w.fontFamily)
                        put("alignment", w.alignment)
                        put("visible", if (w.visible) 1 else 0)
                        put("opacity", w.opacity)
                    }
                    db.insertWithOnConflict("widget_settings", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
            widgetsTrigger.value = System.currentTimeMillis()
        }

        override suspend fun update(widgetSettings: WidgetSettingsEntity) {
            insert(widgetSettings)
        }

        override suspend fun delete(widgetSettings: WidgetSettingsEntity) {
            deleteById(widgetSettings.id)
        }

        override suspend fun deleteById(id: String) {
            writableDatabase.delete("widget_settings", "id = ?", arrayOf(id))
            widgetsTrigger.value = System.currentTimeMillis()
        }

        override suspend fun deleteByDesignId(designId: String) {
            writableDatabase.delete("widget_settings", "designId = ?", arrayOf(designId))
            widgetsTrigger.value = System.currentTimeMillis()
        }

        override fun getByDesignId(designId: String): Flow<List<WidgetSettingsEntity>> {
            return widgetsTrigger.map {
                val list = mutableListOf<WidgetSettingsEntity>()
                val cursor = readableDatabase.query("widget_settings", null, "designId = ?", arrayOf(designId), null, null, "widgetType ASC")
                while (cursor.moveToNext()) {
                    list.add(cursorToWidget(cursor))
                }
                cursor.close()
                list
            }
        }

        override suspend fun getById(id: String): WidgetSettingsEntity? {
            val cursor = readableDatabase.query("widget_settings", null, "id = ?", arrayOf(id), null, null, null)
            val result = if (cursor.moveToFirst()) cursorToWidget(cursor) else null
            cursor.close()
            return result
        }
    }

    private fun cursorToDesign(c: Cursor) = DesignEntity(
        id = c.getString(c.getColumnIndexOrThrow("id")),
        name = c.getString(c.getColumnIndexOrThrow("name")),
        imagePath = c.getString(c.getColumnIndexOrThrow("imagePath")),
        optimizedImagePath = c.getString(c.getColumnIndexOrThrow("optimizedImagePath")),
        canvasWidth = c.getInt(c.getColumnIndexOrThrow("canvasWidth")),
        canvasHeight = c.getInt(c.getColumnIndexOrThrow("canvasHeight")),
        canvasShape = ScreenShape.valueOf(c.getString(c.getColumnIndexOrThrow("canvasShape"))),
        targetDeviceId = c.getString(c.getColumnIndexOrThrow("targetDeviceId")),
        createdAt = c.getLong(c.getColumnIndexOrThrow("createdAt")),
        modifiedAt = c.getLong(c.getColumnIndexOrThrow("modifiedAt"))
    )

    private fun cursorToDevice(c: Cursor) = DeviceEntity(
        id = c.getString(c.getColumnIndexOrThrow("id")),
        bluetoothAddress = c.getString(c.getColumnIndexOrThrow("bluetoothAddress")),
        name = c.getString(c.getColumnIndexOrThrow("name")),
        brand = c.getString(c.getColumnIndexOrThrow("brand")),
        model = c.getString(c.getColumnIndexOrThrow("model")),
        screenWidth = c.getInt(c.getColumnIndexOrThrow("screenWidth")),
        screenHeight = c.getInt(c.getColumnIndexOrThrow("screenHeight")),
        screenShape = ScreenShape.valueOf(c.getString(c.getColumnIndexOrThrow("screenShape"))),
        compatibilityStatus = c.getString(c.getColumnIndexOrThrow("compatibilityStatus")),
        installMethod = c.getString(c.getColumnIndexOrThrow("installMethod")),
        lastConnectedAt = if (c.isNull(c.getColumnIndexOrThrow("lastConnectedAt"))) null else c.getLong(c.getColumnIndexOrThrow("lastConnectedAt")),
        isPrimary = c.getInt(c.getColumnIndexOrThrow("isPrimary")) == 1
    )

    private fun cursorToLayer(c: Cursor) = LayerEntity(
        id = c.getString(c.getColumnIndexOrThrow("id")),
        designId = c.getString(c.getColumnIndexOrThrow("designId")),
        type = LayerType.valueOf(c.getString(c.getColumnIndexOrThrow("type"))),
        posX = c.getFloat(c.getColumnIndexOrThrow("posX")),
        posY = c.getFloat(c.getColumnIndexOrThrow("posY")),
        width = c.getInt(c.getColumnIndexOrThrow("width")),
        height = c.getInt(c.getColumnIndexOrThrow("height")),
        rotation = c.getFloat(c.getColumnIndexOrThrow("rotation")),
        zIndex = c.getInt(c.getColumnIndexOrThrow("zIndex")),
        opacity = c.getFloat(c.getColumnIndexOrThrow("opacity")),
        visible = c.getInt(c.getColumnIndexOrThrow("visible")) == 1,
        locked = c.getInt(c.getColumnIndexOrThrow("locked")) == 1,
        data = c.getString(c.getColumnIndexOrThrow("data"))
    )

    private fun cursorToClock(c: Cursor) = ClockSettingsEntity(
        id = c.getString(c.getColumnIndexOrThrow("id")),
        designId = c.getString(c.getColumnIndexOrThrow("designId")),
        clockType = ClockType.valueOf(c.getString(c.getColumnIndexOrThrow("clockType"))),
        fontFamily = c.getString(c.getColumnIndexOrThrow("fontFamily")),
        fontSize = c.getInt(c.getColumnIndexOrThrow("fontSize")),
        fontWeight = c.getString(c.getColumnIndexOrThrow("fontWeight")),
        textColor = c.getLong(c.getColumnIndexOrThrow("textColor")),
        posX = c.getFloat(c.getColumnIndexOrThrow("posX")),
        posY = c.getFloat(c.getColumnIndexOrThrow("posY")),
        timeFormat = TimeFormat.valueOf(c.getString(c.getColumnIndexOrThrow("timeFormat"))),
        showSeconds = c.getInt(c.getColumnIndexOrThrow("showSeconds")) == 1,
        showAmPm = c.getInt(c.getColumnIndexOrThrow("showAmPm")) == 1,
        leadingZero = c.getInt(c.getColumnIndexOrThrow("leadingZero")) == 1,
        shadowEnabled = c.getInt(c.getColumnIndexOrThrow("shadowEnabled")) == 1,
        glowEnabled = c.getInt(c.getColumnIndexOrThrow("glowEnabled")) == 1,
        outlineEnabled = c.getInt(c.getColumnIndexOrThrow("outlineEnabled")) == 1,
        opacity = c.getFloat(c.getColumnIndexOrThrow("opacity")),
        rotation = c.getFloat(c.getColumnIndexOrThrow("rotation")),
        backgroundColor = if (c.isNull(c.getColumnIndexOrThrow("backgroundColor"))) null else c.getLong(c.getColumnIndexOrThrow("backgroundColor")),
        backgroundRadius = c.getInt(c.getColumnIndexOrThrow("backgroundRadius"))
    )

    private fun cursorToWidget(c: Cursor) = WidgetSettingsEntity(
        id = c.getString(c.getColumnIndexOrThrow("id")),
        designId = c.getString(c.getColumnIndexOrThrow("designId")),
        widgetType = WidgetType.valueOf(c.getString(c.getColumnIndexOrThrow("widgetType"))),
        posX = c.getFloat(c.getColumnIndexOrThrow("posX")),
        posY = c.getFloat(c.getColumnIndexOrThrow("posY")),
        width = c.getInt(c.getColumnIndexOrThrow("width")),
        height = c.getInt(c.getColumnIndexOrThrow("height")),
        textColor = c.getLong(c.getColumnIndexOrThrow("textColor")),
        fontSize = c.getInt(c.getColumnIndexOrThrow("fontSize")),
        fontFamily = c.getString(c.getColumnIndexOrThrow("fontFamily")),
        alignment = c.getString(c.getColumnIndexOrThrow("alignment")),
        visible = c.getInt(c.getColumnIndexOrThrow("visible")) == 1,
        opacity = c.getFloat(c.getColumnIndexOrThrow("opacity"))
    )
}
