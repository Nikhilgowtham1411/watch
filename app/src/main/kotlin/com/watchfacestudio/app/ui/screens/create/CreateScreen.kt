package com.watchfacestudio.app.ui.screens.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watchfacestudio.app.ui.appViewModel
import com.watchfacestudio.app.ui.components.GlassCard
import com.watchfacestudio.app.ui.components.WatchPreview
import com.watchfacestudio.app.ui.theme.Background
import com.watchfacestudio.app.ui.theme.CardBackground
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceShapes
import com.watchfacestudio.app.ui.theme.WatchFaceTypography

@Composable
fun CreateScreen(
    onBackClick: () -> Unit,
    onPhotoSelected: (Uri) -> Unit,
    onTemplateSelected: (String) -> Unit,
    viewModel: CreateViewModel = appViewModel { CreateViewModel() }
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val filteredTemplates = viewModel.getFilteredTemplates()

    // Android Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onPhotoSelected(uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Create Watch Face",
                style = WatchFaceTypography.titleMedium,
                color = TextPrimary
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // "From Gallery" Card (Full Width)
            item(span = { GridItemSpan(2) }) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    shape = WatchFaceShapes.large
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "From Gallery",
                                style = WatchFaceTypography.titleMedium,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Use your own photo",
                                style = WatchFaceTypography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }
            }

            // "AI Generate" Card (Full Width)
            item(span = { GridItemSpan(2) }) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Can open template or editor with AI art preset
                            onTemplateSelected("template-aurora")
                        },
                    shape = WatchFaceShapes.large
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEC4899).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFF472B6),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI Generate",
                                style = WatchFaceTypography.titleMedium,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Create with generative prompt",
                                style = WatchFaceTypography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }
            }

            // Category Title + Horizontal Filter Chips (Full Width)
            item(span = { GridItemSpan(2) }) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Or Choose a Template",
                        style = WatchFaceTypography.titleSmall,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.categories) { cat ->
                            val isSelected = cat == selectedCategory
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSelected) Primary else SecondaryBackground)
                                    .clickable { viewModel.setCategory(cat) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = cat,
                                    style = WatchFaceTypography.labelMedium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Template Cards (2-column Grid)
            items(filteredTemplates) { template ->
                TemplateCard(
                    template = template,
                    onClick = { onTemplateSelected(template.id) }
                )
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun TemplateCard(
    template: WatchTemplate,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            WatchPreview(
                screenShape = template.shape,
                modifier = Modifier.size(105.dp),
                bezelWidth = 6.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(template.backgroundColor)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = template.timeText,
                            style = WatchFaceTypography.headlineMedium.copy(fontSize = 18.sp),
                            color = Color(template.primaryColor)
                        )
                        Text(
                            text = template.dateText,
                            style = WatchFaceTypography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(template.primaryColor).copy(alpha = 0.7f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = template.name,
                style = WatchFaceTypography.labelMedium,
                color = TextPrimary
            )
        }
    }
}
