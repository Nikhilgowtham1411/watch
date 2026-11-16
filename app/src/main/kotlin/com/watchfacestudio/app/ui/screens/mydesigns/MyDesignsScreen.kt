package com.watchfacestudio.app.ui.screens.mydesigns

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.watchfacestudio.app.WatchFaceStudioApp
import com.watchfacestudio.app.ui.appViewModel
import com.watchfacestudio.app.data.local.db.entities.DesignEntity
import com.watchfacestudio.app.ui.components.EmptyState
import com.watchfacestudio.app.ui.components.PrimaryButton
import com.watchfacestudio.app.ui.components.WatchPreview
import com.watchfacestudio.app.ui.theme.Background
import com.watchfacestudio.app.ui.theme.CardBackground
import com.watchfacestudio.app.ui.theme.Primary
import com.watchfacestudio.app.ui.theme.SecondaryBackground
import com.watchfacestudio.app.ui.theme.TextPrimary
import com.watchfacestudio.app.ui.theme.TextSecondary
import com.watchfacestudio.app.ui.theme.WatchFaceTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyDesignsScreen(
    onCreateNewClick: () -> Unit,
    onDesignClick: (String) -> Unit,
    onApplyClick: (String) -> Unit,
    viewModel: MyDesignsViewModel = appViewModel {
        MyDesignsViewModel(WatchFaceStudioApp.instance.container.designRepository)
    }
) {
    val designs by viewModel.designs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Title Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Designs",
                style = WatchFaceTypography.headlineMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onCreateNewClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create New",
                    tint = Primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Search designs...", color = TextSecondary) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SecondaryBackground,
                unfocusedContainerColor = SecondaryBackground,
                focusedBorderColor = Primary,
                unfocusedBorderColor = Color(0x22FFFFFF),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (designs.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Palette,
                title = "No Designs Yet",
                description = "Start creating custom watch faces with your personal photos and widgets.",
                actionText = "Create Watch Face",
                onActionClick = onCreateNewClick,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(designs) { design ->
                    SavedDesignCard(
                        design = design,
                        onClick = { onDesignClick(design.id) },
                        onApply = { onApplyClick(design.id) },
                        onDuplicate = { viewModel.duplicateDesign(design.id) },
                        onDelete = { viewModel.deleteDesign(design.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedDesignCard(
    design: DesignEntity,
    onClick: () -> Unit,
    onApply: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            WatchPreview(
                screenShape = design.canvasShape,
                modifier = Modifier.size(100.dp),
                bezelWidth = 6.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "10:08",
                        style = WatchFaceTypography.headlineMedium.copy(fontSize = 18.sp),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = design.name,
                        style = WatchFaceTypography.titleSmall,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(design.modifiedAt)),
                        style = WatchFaceTypography.labelSmall,
                        color = TextSecondary
                    )
                }

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(CardBackground)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Apply to Watch", color = Primary) },
                            onClick = {
                                menuExpanded = false
                                onApply()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Duplicate", color = TextPrimary) },
                            onClick = {
                                menuExpanded = false
                                onDuplicate()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color(0xFFEF4444)) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}
