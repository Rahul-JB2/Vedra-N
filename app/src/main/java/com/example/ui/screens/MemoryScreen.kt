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
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.services.AppMapping
import com.example.services.ContactAlias
import com.example.services.DatabaseService
import com.example.services.UserMemory
import com.example.ui.components.CustomButton
import com.example.ui.components.CustomCard
import com.example.ui.components.CustomInput
import com.example.ui.components.CustomModal
import com.example.ui.theme.Spacing
import com.example.ui.theme.VedraBackground
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
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Facts/Context, 1: Contact Aliases, 2: App Shortcuts
    var searchQuery by remember { mutableStateOf("") }

    val userMemories = remember { mutableStateListOf<UserMemory>() }
    val contactAliases = remember { mutableStateListOf<ContactAlias>() }
    val appMappings = remember { mutableStateListOf<AppMapping>() }

    // Modal state for Memory
    var isAddMemoryModalOpen by remember { mutableStateOf(false) }
    var inputMemoryKey by remember { mutableStateOf("") }
    var inputMemoryVal by remember { mutableStateOf("") }

    // Modal state for Alias
    var isAddAliasModalOpen by remember { mutableStateOf(false) }
    var inputAliasName by remember { mutableStateOf("") }
    var inputAliasTarget by remember { mutableStateOf("") }

    // Modal state for Mapping
    var isAddMappingModalOpen by remember { mutableStateOf(false) }
    var inputCustomWord by remember { mutableStateOf("") }
    var inputAppIdentifier by remember { mutableStateOf("") }

    fun refreshAll() {
        userMemories.clear()
        userMemories.addAll(dbService.getAllMemories())

        contactAliases.clear()
        contactAliases.addAll(dbService.getAllAliases())

        appMappings.clear()
        appMappings.addAll(dbService.getAllMappings())
    }

    LaunchedEffect(Unit) {
        refreshAll()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VedraBackground)
            .padding(horizontal = Spacing.medium),
        contentPadding = PaddingValues(vertical = Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        // Header & Stats
        item {
            Column {
                Text(
                    text = "MEMORY & ALIAS CONTROL",
                    color = VedraTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "Manage User Facts, Contact Aliases & App Shortcuts",
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
                MemoryStatBox(title = "Facts", value = userMemories.size.toString(), color = VedraPurplePrimary, modifier = Modifier.weight(1f))
                MemoryStatBox(title = "Aliases", value = contactAliases.size.toString(), color = VedraCyanAccent, modifier = Modifier.weight(1f))
                MemoryStatBox(title = "Shortcuts", value = appMappings.size.toString(), color = VedraPinkAccent, modifier = Modifier.weight(1f))
            }
        }

        // Sub-tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VedraSurface, RoundedCornerShape(12.dp))
                    .border(1.dp, VedraBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val subTabs = listOf("Facts & Context", "Contact Aliases", "App Shortcuts")
                subTabs.forEachIndexed { idx, title ->
                    val isSelected = activeSubTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) VedraPurplePrimary else Color.Transparent)
                            .clickable { activeSubTab = idx }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) VedraTextPrimary else VedraTextMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item {
            CustomInput(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search memories...",
                leadingIcon = Icons.Default.Search
            )
        }

        // SUB-TAB 0: FACTS & CONTEXT
        if (activeSubTab == 0) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SAVED USER FACTS",
                        color = VedraTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    CustomButton(
                        text = "Add Fact",
                        icon = Icons.Default.Add,
                        onClick = {
                            inputMemoryKey = ""
                            inputMemoryVal = ""
                            isAddMemoryModalOpen = true
                        },
                        modifier = Modifier.height(34.dp)
                    )
                }
            }

            val filteredFacts = userMemories.filter {
                it.memoryKey.contains(searchQuery, ignoreCase = true) ||
                        it.memoryValue.contains(searchQuery, ignoreCase = true)
            }

            if (filteredFacts.isEmpty()) {
                item {
                    Text(text = "No user facts saved yet.", color = VedraTextMuted, fontSize = 13.sp)
                }
            } else {
                items(filteredFacts, key = { it.id }) { mem ->
                    CustomCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = VedraPurpleSecondary)
                                Spacer(modifier = Modifier.width(Spacing.medium))
                                Column {
                                    Text(text = mem.memoryKey, color = VedraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = mem.memoryValue, color = VedraCyanAccent, fontSize = 12.sp)
                                }
                            }

                            IconButton(
                                onClick = {
                                    dbService.deleteMemory(mem.id)
                                    refreshAll()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = VedraPinkAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // SUB-TAB 1: CONTACT ALIASES
        if (activeSubTab == 1) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CONTACT ALIASES",
                        color = VedraTextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    CustomButton(
                        text = "Add Alias",
                        icon = Icons.Default.Add,
                        onClick = {
                            inputAliasName = ""
                            inputAliasTarget = ""
                            isAddAliasModalOpen = true
                        },
                        modifier = Modifier.height(34.dp)
                    )
                }
            }

            val filteredAliases = contactAliases.filter {
                it.aliasName.contains(searchQuery, ignoreCase = true) ||
                        it.targetContactOrNumber.contains(searchQuery, ignoreCase = true)
            }

            if (filteredAliases.isEmpty()) {
                item {
                    Text(text = "No contact aliases defined.", color = VedraTextMuted, fontSize = 13.sp)
                }
            } else {
                items(filteredAliases, key = { it.id }) { alias ->
                    CustomCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.ContactPage, contentDescription = null, tint = VedraCyanAccent)
                                Spacer(modifier = Modifier.width(Spacing.medium))
                                Column {
                                    Text(text = "\"${alias.aliasName}\"", color = VedraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Target: ${alias.targetContactOrNumber}", color = VedraTextMuted, fontSize = 12.sp)
                                }
                            }

                            IconButton(
                                onClick = {
                                    dbService.deleteAlias(alias.id)
                                    refreshAll()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = VedraPinkAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // SUB-TAB 2: APP SHORTCUTS
        if (activeSubTab == 2) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "APP SHORTCUTS",
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
                            isAddMappingModalOpen = true
                        },
                        modifier = Modifier.height(34.dp)
                    )
                }
            }

            val filteredMappings = appMappings.filter {
                it.customWord.contains(searchQuery, ignoreCase = true) ||
                        it.appIdentifier.contains(searchQuery, ignoreCase = true)
            }

            items(filteredMappings, key = { it.id }) { mapping ->
                CustomCard(onClick = { onTestLaunch(mapping.customWord) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(VedraSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Apps, contentDescription = null, tint = VedraPurpleSecondary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(Spacing.medium))
                            Column {
                                Text(text = mapping.customWord, color = VedraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = mapping.appIdentifier, color = VedraTextMuted, fontSize = 11.sp)
                            }
                        }

                        IconButton(
                            onClick = {
                                dbService.deleteMapping(mapping.id)
                                refreshAll()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = VedraPinkAccent, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    // Modal for Add Memory
    CustomModal(
        visible = isAddMemoryModalOpen,
        title = "Add User Memory Fact",
        onDismissRequest = { isAddMemoryModalOpen = false }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            CustomInput(value = inputMemoryKey, onValueChange = { inputMemoryKey = it }, placeholder = "Memory Key (e.g. My school)")
            CustomInput(value = inputMemoryVal, onValueChange = { inputMemoryVal = it }, placeholder = "Memory Value (e.g. BSEB Class 12)")
            CustomButton(
                text = "Save Memory",
                onClick = {
                    if (inputMemoryKey.isNotBlank() && inputMemoryVal.isNotBlank()) {
                        dbService.addOrUpdateMemory(inputMemoryKey, inputMemoryVal)
                        refreshAll()
                        isAddMemoryModalOpen = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Modal for Add Alias
    CustomModal(
        visible = isAddAliasModalOpen,
        title = "Add Contact Alias",
        onDismissRequest = { isAddAliasModalOpen = false }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            CustomInput(value = inputAliasName, onValueChange = { inputAliasName = it }, placeholder = "Alias Name (e.g. Mom, Bestie)")
            CustomInput(value = inputAliasTarget, onValueChange = { inputAliasTarget = it }, placeholder = "Phone Number / Contact Name (e.g. +91 9876543210)")
            CustomButton(
                text = "Save Alias",
                onClick = {
                    if (inputAliasName.isNotBlank() && inputAliasTarget.isNotBlank()) {
                        dbService.addOrUpdateAlias(inputAliasName, inputAliasTarget)
                        refreshAll()
                        isAddAliasModalOpen = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Modal for Add App Mapping
    CustomModal(
        visible = isAddMappingModalOpen,
        title = "Add Custom App Shortcut",
        onDismissRequest = { isAddMappingModalOpen = false }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            CustomInput(value = inputCustomWord, onValueChange = { inputCustomWord = it }, placeholder = "Custom Word (e.g. notes)")
            CustomInput(value = inputAppIdentifier, onValueChange = { inputAppIdentifier = it }, placeholder = "Package ID (e.g. com.google.android.keep)")
            CustomButton(
                text = "Save Shortcut",
                onClick = {
                    if (inputCustomWord.isNotBlank() && inputAppIdentifier.isNotBlank()) {
                        dbService.addOrUpdateMapping(inputCustomWord, inputAppIdentifier)
                        refreshAll()
                        isAddMappingModalOpen = false
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
