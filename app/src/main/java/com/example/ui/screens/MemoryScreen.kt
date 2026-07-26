package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.services.AppMapping
import com.example.services.DatabaseService
import com.example.ui.components.CustomButton
import com.example.ui.components.CustomCard
import com.example.ui.components.CustomInput
import com.example.ui.components.CustomModal
import com.example.ui.theme.Spacing
import com.example.ui.theme.VedraBackground
import com.example.ui.theme.VedraBlueAccent
import com.example.ui.theme.VedraBorder
import com.example.ui.theme.VedraCyanAccent
import com.example.ui.theme.VedraPinkAccent
import com.example.ui.theme.VedraPurplePrimary
import com.example.ui.theme.VedraPurpleSecondary
import com.example.ui.theme.VedraSurface
import com.example.ui.theme.VedraSurfaceVariant
import com.example.ui.theme.VedraTextMuted
import com.example.ui.theme.VedraTextPrimary
import com.example.ui.theme.VedraTextSecondary

@Composable
fun MemoryScreen(
    dbService: DatabaseService,
    onTestLaunch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val appMappings = remember { mutableStateListOf<AppMapping>() }

    var isAddModalOpen by remember { mutableStateOf(false) }
    var editingMapping by remember { mutableStateOf<AppMapping?>(null) }

    var inputCustomWord by remember { mutableStateOf("") }
    var inputAppIdentifier by remember { mutableStateOf("") }

    // Load mappings from DB
    fun refreshMappings() {
        appMappings.clear()
        appMappings.addAll(dbService.getAllMappings())
    }

    LaunchedEffect(Unit) {
        refreshMappings()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VedraBackground)
            .padding(horizontal = Spacing.medium),
        contentPadding = PaddingValues(vertical = Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        // Top Header & Stats
        item {
            Column {
                Text(
                    text = "MEMORY & MAPPINGS",
                    color = VedraTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "What Vedra remembers & Custom Word App Launcher",
                    color = VedraTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                MemoryStatBox(title = "Memories", value = "128", color = VedraPurplePrimary, modifier = Modifier.weight(1f))
                MemoryStatBox(title = "Preferences", value = "24", color = VedraCyanAccent, modifier = Modifier.weight(1f))
                MemoryStatBox(title = "Shortcuts", value = appMappings.size.toString(), color = VedraPinkAccent, modifier = Modifier.weight(1f))
            }
        }

        // Search & Add Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CUSTOM APP SHORTCUTS",
                    color = VedraTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                CustomButton(
                    text = "Add Shortcut",
                    icon = Icons.Default.Add,
                    onClick = {
                        inputCustomWord = ""
                        inputAppIdentifier = ""
                        editingMapping = null
                        isAddModalOpen = true
                    },
                    modifier = Modifier.height(34.dp)
                )
            }
        }

        item {
            CustomInput(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search custom words or packages...",
                leadingIcon = Icons.Default.Search
            )
        }

        // List of Mappings
        val filtered = appMappings.filter {
            it.customWord.contains(searchQuery, ignoreCase = true) ||
                    it.appIdentifier.contains(searchQuery, ignoreCase = true)
        }

        if (filtered.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.large),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No app shortcuts found.", color = VedraTextMuted, fontSize = 14.sp)
                }
            }
        } else {
            items(filtered, key = { it.id }) { mapping ->
                CustomCard(
                    onClick = { onTestLaunch(mapping.customWord) },
                    testTag = "mapping_card_${mapping.customWord}"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(VedraSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = null,
                                    tint = VedraPurpleSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(Spacing.medium))
                            Column {
                                Text(
                                    text = mapping.customWord,
                                    color = VedraTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = mapping.appIdentifier,
                                    color = VedraTextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    editingMapping = mapping
                                    inputCustomWord = mapping.customWord
                                    inputAppIdentifier = mapping.appIdentifier
                                    isAddModalOpen = true
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = VedraCyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    dbService.deleteMapping(mapping.id)
                                    refreshMappings()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = VedraPinkAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Timeline Section
        item {
            Text(
                text = "RECENT MEMORY LOGS",
                color = VedraTextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = Spacing.medium)
            )
        }

        val timelineItems = listOf(
            "Shortcut \"whatsapp\" linked to com.whatsapp",
            "Shortcut \"camera\" linked to com.android.camera",
            "User preference saved: Dark Theme & Voice Mode enabled",
            "Nickname saved: Shalini -> Mom"
        )

        items(timelineItems) { log ->
            CustomCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = VedraPurplePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.medium))
                    Text(text = log, color = VedraTextPrimary, fontSize = 13.sp)
                }
            }
        }
    }

    // Modal for Add/Edit Mapping using CustomModal
    CustomModal(
        visible = isAddModalOpen,
        title = if (editingMapping == null) "Add Custom App Mapping" else "Edit App Mapping",
        onDismissRequest = { isAddModalOpen = false }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            CustomInput(
                value = inputCustomWord,
                onValueChange = { inputCustomWord = it },
                placeholder = "Custom Word (e.g. chat, music, notes)"
            )
            CustomInput(
                value = inputAppIdentifier,
                onValueChange = { inputAppIdentifier = it },
                placeholder = "App Package ID (e.g. com.whatsapp)"
            )

            CustomButton(
                text = if (editingMapping == null) "Save Mapping" else "Update Mapping",
                onClick = {
                    if (inputCustomWord.isNotBlank() && inputAppIdentifier.isNotBlank()) {
                        dbService.addOrUpdateMapping(inputCustomWord, inputAppIdentifier)
                        refreshMappings()
                        isAddModalOpen = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MemoryStatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    CustomCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, color = VedraTextMuted, fontSize = 11.sp)
        }
    }
}
