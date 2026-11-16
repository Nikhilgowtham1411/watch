package com.watchfacestudio.app.ui.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val PresetPalette = listOf(
    Color(0xFFFFFFFF),
    Color(0xFF8B5CF6),
    Color(0xFFA78BFA),
    Color(0xFF38BDF8),
    Color(0xFF34D399),
    Color(0xFFFBBF24),
    Color(0xFFFB923C),
    Color(0xFFF87171),
    Color(0xFFF472B6),
    Color(0xFFA3E635),
    Color(0xFF2DD4BF)
)

@Composable
fun ColorPickerRow(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(PresetPalette) { color ->
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) Color.White else Color(0x33FFFFFF),
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}
