package com.watchfacestudio.app.ui.screens.create

import androidx.lifecycle.ViewModel
import com.watchfacestudio.app.data.model.ScreenShape
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class WatchTemplate(
    val id: String,
    val name: String,
    val category: String,
    val timeText: String,
    val dateText: String,
    val shape: ScreenShape,
    val isAnalog: Boolean = false,
    val primaryColor: Long = 0xFF8B5CF6,
    val backgroundColor: Long = 0xFF14141E
)

class CreateViewModel : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories = listOf("All", "Digital", "Analog", "Minimal", "Nature", "AMOLED")

    val templates = listOf(
        WatchTemplate(
            id = "template-sunset",
            name = "Alpine Sunset",
            category = "Nature",
            timeText = "10:08",
            dateText = "Mon, Nov 16",
            shape = ScreenShape.RECTANGULAR,
            primaryColor = 0xFFFFFFFF,
            backgroundColor = 0xFF2A1B28
        ),
        WatchTemplate(
            id = "template-chrono",
            name = "Chrono Minimal",
            category = "Analog",
            timeText = "10:08",
            dateText = "16",
            shape = ScreenShape.CIRCULAR,
            isAnalog = true,
            primaryColor = 0xFFA78BFA,
            backgroundColor = 0xFF0D0D15
        ),
        WatchTemplate(
            id = "template-cyber",
            name = "Cyber Digital",
            category = "Digital",
            timeText = "11:08",
            dateText = "MON",
            shape = ScreenShape.RECTANGULAR,
            primaryColor = 0xFF8B5CF6,
            backgroundColor = 0xFF12121C
        ),
        WatchTemplate(
            id = "template-moon",
            name = "Lunar Eclipse",
            category = "Minimal",
            timeText = "10:08",
            dateText = "Mon 16",
            shape = ScreenShape.CIRCULAR,
            primaryColor = 0xFFE0E7FF,
            backgroundColor = 0xFF05050A
        ),
        WatchTemplate(
            id = "template-floral",
            name = "Orchid Bloom",
            category = "Nature",
            timeText = "10:08",
            dateText = "Nov 16",
            shape = ScreenShape.RECTANGULAR,
            primaryColor = 0xFFF472B6,
            backgroundColor = 0xFF2D1525
        ),
        WatchTemplate(
            id = "template-aurora",
            name = "Aurora Glow",
            category = "AMOLED",
            timeText = "10:08",
            dateText = "Mon 16",
            shape = ScreenShape.RECTANGULAR,
            primaryColor = 0xFF34D399,
            backgroundColor = 0xFF051515
        )
    )

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun getFilteredTemplates(): List<WatchTemplate> {
        val cat = _selectedCategory.value
        return if (cat == "All") templates else templates.filter { it.category == cat }
    }
}
