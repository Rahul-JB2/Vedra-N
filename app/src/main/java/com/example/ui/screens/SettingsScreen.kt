package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.services.CustomPlugin
import com.example.services.CustomRoutine
import com.example.services.DatabaseService
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
import com.example.ui.theme.VedraTextMuted
import com.example.ui.theme.VedraTextPrimary
import com.example.ui.theme.VedraTextSecondary
import org.json.JSONArray

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.services.GoogleDriveService
import com.example.services.PermissionService
import com.example.services.PermissionStatus
import com.example.services.TranslationService
import com.example.services.VoiceService

@Composable
fun SettingsScreen(
    dbService: DatabaseService,
    voiceService: VoiceService? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isVoiceSettingsModalOpen by remember { mutableStateOf(false) }
    var isLanguageModalOpen by remember { mutableStateOf(false) }
    var selectedLang by remember { mutableStateOf(TranslationService.getTargetLanguage()) }
    var speechSpeed by remember { mutableFloatStateOf(1.0f) }
    var wakeWordEnabled by remember { mutableStateOf(true) }
    var floatingWidgetEnabled by remember { mutableStateOf(true) }

    // Phase 16 State Variables
    var speechPitch by remember { mutableFloatStateOf(dbService.getSetting("pitch", "1.0").toFloatOrNull() ?: 1.0f) }
    var speechSpeedRate by remember { mutableFloatStateOf(dbService.getSetting("speed", "1.0").toFloatOrNull() ?: 1.0f) }
    var selectedTone by remember { mutableStateOf(dbService.getSetting("tone", "Short & Direct")) }
    var selectedEngine by remember { mutableStateOf(dbService.getSetting("engine", "Hybrid Cloud AI")) }
    var customApiKey by remember { mutableStateOf(dbService.getSetting("api_key", "")) }
    var customApiUrl by remember { mutableStateOf(dbService.getSetting("api_url", "https://api.openai.com/v1")) }
    var clearConfirmSection by remember { mutableStateOf<String?>(null) }

    // Task Chains / Routines State
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    var isDriveSyncing by remember { mutableStateOf(false) }
    var driveSyncMessage by remember { mutableStateOf<String?>(null) }

    var isAddRoutineModalOpen by remember { mutableStateOf(false) }
    var routineTriggerInput by remember { mutableStateOf("") }
    var routineActionsInput by remember { mutableStateOf("") } // Comma separated actions

    // Custom Webhook / API Plugins State
    var isAddPluginModalOpen by remember { mutableStateOf(false) }
    var pluginNameInput by remember { mutableStateOf("") }
    var pluginUrlInput by remember { mutableStateOf("") }
    var pluginHeadersInput by remember { mutableStateOf("") }
    var pluginTriggerInput by remember { mutableStateOf("") }

    val routines = remember { mutableStateListOf<CustomRoutine>() }
    val plugins = remember { mutableStateListOf<CustomPlugin>() }
    var permissionsList by remember { mutableStateOf<List<PermissionStatus>>(emptyList()) }

    val permLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionsList = PermissionService.checkAllPermissions(context)
    }

    fun refreshRoutines() {
        routines.clear()
        routines.addAll(dbService.getAllRoutines())
    }

    fun refreshPlugins() {
        plugins.clear()
        plugins.addAll(dbService.getAllPlugins())
    }

    LaunchedEffect(Unit) {
        refreshRoutines()
        refreshPlugins()
        permissionsList = PermissionService.checkAllPermissions(context)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VedraBackground)
            .padding(horizontal = Spacing.medium),
        contentPadding = PaddingValues(vertical = Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        item {
            Text(
                text = "SETTINGS & ROUTINES",
                color = VedraTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Customize VEDRA & Configure Task Chains",
                color = VedraTextSecondary,
                fontSize = 12.sp
            )
        }

        // Vedra Pro Card
        item {
            CustomCard(borderColor = VedraPurplePrimary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = VedraPurpleSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Vedra Pro Active",
                                color = VedraTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Offline AI, task chains, voice assistant & custom triggers.",
                            color = VedraTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    CustomButton(
                        text = "Manage",
                        onClick = { },
                        isSecondary = true,
                        modifier = Modifier.height(34.dp)
                    )
                }
            }
        }

        // OFFLINE STORAGE & SYNC STATUS CARD
        item {
            val stats = dbService.getOfflineStorageStats()
            val totalSaved = stats.values.sum()

            CustomCard(borderColor = VedraCyanAccent) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "OFFLINE SYNC & STORAGE STATUS",
                                color = VedraCyanAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "$totalSaved Records Saved",
                            color = Color(0xFF81C784),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All interactions, habits, study notes, and custom aliases are stored 100% locally on your device for full offline autonomy.",
                        color = VedraTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        stats.entries.chunked(2).forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                pair.forEach { entry ->
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = entry.key, color = VedraTextMuted, fontSize = 11.sp)
                                        Text(text = entry.value.toString(), color = VedraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    var backupStatusMessage by remember { mutableStateOf("") }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CustomButton(
                            text = "Export Backup",
                            onClick = {
                                val json = dbService.exportBackupJson()
                                backupStatusMessage = "Backup Exported! (${json.length} bytes JSON)"
                            },
                            modifier = Modifier.weight(1f).height(36.dp)
                        )
                        CustomButton(
                            text = "Restore Backup",
                            onClick = {
                                val currentJson = dbService.exportBackupJson()
                                val success = dbService.restoreBackupJson(currentJson)
                                backupStatusMessage = if (success) "Backup Restored Successfully! ✅" else "Restore failed!"
                            },
                            modifier = Modifier.weight(1f).height(36.dp)
                        )
                    }

                    if (backupStatusMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = backupStatusMessage,
                            color = Color(0xFF81C784),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // PERSONAL GMAIL DRIVE SYNC CARD
        item {
            CustomCard(borderColor = VedraCyanAccent) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "☁️ PERSONAL GMAIL DRIVE SYNC",
                            color = VedraCyanAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (GoogleDriveService.isConnected(dbService)) "Connected" else "Disconnected",
                            color = if (GoogleDriveService.isConnected(dbService)) Color(0xFF4CAF50) else VedraPinkAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    val connectedEmail = GoogleDriveService.getConnectedEmail(dbService)
                    val lastSync = GoogleDriveService.getLastSyncTime(dbService)

                    Text(
                        text = "Account: $connectedEmail\nFolder: VEDRA_AI_Memories • Last Sync: $lastSync",
                        color = VedraTextSecondary,
                        fontSize = 11.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CustomButton(
                            text = if (GoogleDriveService.isConnected(dbService)) "Disconnect" else "Connect Gmail",
                            onClick = {
                                if (GoogleDriveService.isConnected(dbService)) {
                                    GoogleDriveService.disconnectAccount(dbService)
                                    driveSyncMessage = "Disconnected Google Drive."
                                } else {
                                    GoogleDriveService.connectAccount(dbService, "rk70502025@gmail.com")
                                    driveSyncMessage = "Connected Google Drive for rk70502025@gmail.com."
                                }
                            },
                            isSecondary = GoogleDriveService.isConnected(dbService),
                            modifier = Modifier.weight(1f).height(34.dp)
                        )

                        CustomButton(
                            text = if (isDriveSyncing) "Syncing..." else "Back Up Now",
                            onClick = {
                                isDriveSyncing = true
                                coroutineScope.launch {
                                    driveSyncMessage = GoogleDriveService.exportAllMemoriesToDrive(context, dbService)
                                    isDriveSyncing = false
                                }
                            },
                            modifier = Modifier.weight(1f).height(34.dp)
                        )

                        CustomButton(
                            text = "Restore",
                            onClick = {
                                isDriveSyncing = true
                                coroutineScope.launch {
                                    driveSyncMessage = GoogleDriveService.importMemoriesFromDrive(context, dbService)
                                    isDriveSyncing = false
                                }
                            },
                            isSecondary = true,
                            modifier = Modifier.weight(1f).height(34.dp)
                        )
                    }

                    if (driveSyncMessage != null) {
                        Text(
                            text = driveSyncMessage!!,
                            color = VedraTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // AI PERSONALITY CUSTOMIZER CARD
        item {
            CustomCard(borderColor = VedraPurplePrimary) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = VedraPurpleSecondary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI PERSONALITY & VOICE CUSTOMIZER",
                            color = VedraTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // Voice Pitch Slider
                    Text(text = "Voice Pitch: ${"%.2f".format(speechPitch)}x", color = VedraTextSecondary, fontSize = 12.sp)
                    Slider(
                        value = speechPitch,
                        onValueChange = {
                            speechPitch = it
                            voiceService?.setPitchAndRate(speechPitch, speechSpeedRate)
                            dbService.setSetting("pitch", speechPitch.toString())
                        },
                        valueRange = 0.5f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = VedraPurplePrimary, activeTrackColor = VedraPurplePrimary)
                    )

                    // Voice Speed Rate Slider
                    Text(text = "Voice Speed Rate: ${"%.2f".format(speechSpeedRate)}x", color = VedraTextSecondary, fontSize = 12.sp)
                    Slider(
                        value = speechSpeedRate,
                        onValueChange = {
                            speechSpeedRate = it
                            voiceService?.setPitchAndRate(speechPitch, speechSpeedRate)
                            dbService.setSetting("speed", speechSpeedRate.toString())
                        },
                        valueRange = 0.5f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = VedraCyanAccent, activeTrackColor = VedraCyanAccent)
                    )

                    // Personality Tone
                    Text(text = "Assistant Personality Tone", color = VedraTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Short & Direct", "Detailed & Conversational", "Empathetic Coach").forEach { tone ->
                            val isSelected = selectedTone == tone
                            CustomButton(
                                text = tone,
                                onClick = {
                                    selectedTone = tone
                                    dbService.setSetting("tone", tone)
                                },
                                isSecondary = !isSelected,
                                modifier = Modifier.weight(1f).height(32.dp)
                            )
                        }
                    }
                }
            }
        }

        // AI ENGINE SWITCHER CARD
        item {
            CustomCard(borderColor = VedraCyanAccent) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    Text(
                        text = "⚙️ AI ENGINE SWITCHER",
                        color = VedraCyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Choose model execution provider for local vs cloud fallback.",
                        color = VedraTextSecondary,
                        fontSize = 11.sp
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Hybrid Cloud AI", "Strict Offline ONNX", "Custom API Endpoint").forEach { engine ->
                            val isSelected = selectedEngine == engine
                            CustomButton(
                                text = engine,
                                onClick = {
                                    selectedEngine = engine
                                    dbService.setSetting("engine", engine)
                                },
                                isSecondary = !isSelected,
                                modifier = Modifier.weight(1f).height(32.dp)
                            )
                        }
                    }

                    if (selectedEngine == "Custom API Endpoint") {
                        Spacer(modifier = Modifier.height(4.dp))
                        CustomInput(
                            value = customApiKey,
                            onValueChange = {
                                customApiKey = it
                                dbService.setSetting("api_key", it)
                            },
                            placeholder = "Custom API Key (e.g. sk-proj-...)"
                        )
                        CustomInput(
                            value = customApiUrl,
                            onValueChange = {
                                customApiUrl = it
                                dbService.setSetting("api_url", it)
                            },
                            placeholder = "Custom Endpoint URL (e.g. https://api.openai.com/v1)"
                        )
                    }
                }
            }
        }

        // GRANULAR STORAGE INSPECTOR CARD
        item {
            CustomCard(borderColor = VedraPinkAccent) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    Text(
                        text = "🗑️ GRANULAR STORAGE INSPECTOR",
                        color = VedraPinkAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Purge individual SQLite tables without deleting your entire database.",
                        color = VedraTextSecondary,
                        fontSize = 11.sp
                    )

                    val storageItems = listOf(
                        "Notes" to dbService.getAllNotes().size,
                        "Flashcards" to dbService.getAllFlashcards().size,
                        "Habits" to dbService.getAllHabits().size,
                        "Expenses" to dbService.getAllExpenses().size,
                        "Memories" to dbService.getAllMemories().size,
                        "Aliases" to dbService.getAllAliases().size,
                        "Shortcuts" to dbService.getAllMappings().size
                    )

                    storageItems.forEach { (label, count) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "$label: $count records", color = VedraTextPrimary, fontSize = 12.sp)
                            CustomButton(
                                text = "Clear $label",
                                onClick = { clearConfirmSection = label },
                                isSecondary = true,
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // TASK CHAINS / ROUTINES SECTION
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "TASK CHAINS & AUTOMATION", color = VedraTextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                CustomButton(
                    text = "Add Routine",
                    icon = Icons.Default.Add,
                    onClick = {
                        routineTriggerInput = ""
                        routineActionsInput = ""
                        isAddRoutineModalOpen = true
                    },
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        if (routines.isEmpty()) {
            item {
                Text(text = "No custom task chains configured.", color = VedraTextMuted, fontSize = 12.sp)
            }
        } else {
            items(routines, key = { it.id }) { routine ->
                CustomCard(borderColor = VedraCyanAccent) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Repeat, contentDescription = null, tint = VedraCyanAccent)
                            Spacer(modifier = Modifier.width(Spacing.medium))
                            Column {
                                Text(
                                    text = "Trigger: \"${routine.triggerPhrase}\"",
                                    color = VedraTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Actions: ${routine.actionChainJson}",
                                    color = VedraTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                dbService.deleteRoutine(routine.id)
                                refreshRoutines()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = VedraPinkAccent, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // CUSTOM API PLUGINS & WEBHOOKS SECTION
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "CUSTOM API PLUGINS & WEBHOOKS", color = VedraTextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                CustomButton(
                    text = "Add Plugin",
                    icon = Icons.Default.Add,
                    onClick = {
                        pluginNameInput = ""
                        pluginUrlInput = ""
                        pluginHeadersInput = ""
                        pluginTriggerInput = ""
                        isAddPluginModalOpen = true
                    },
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        if (plugins.isEmpty()) {
            item {
                Text(text = "No custom API plugins installed.", color = VedraTextMuted, fontSize = 12.sp)
            }
        } else {
            items(plugins, key = { it.id }) { plugin ->
                CustomCard(borderColor = VedraPurplePrimary) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Widgets, contentDescription = null, tint = VedraPurplePrimary)
                            Spacer(modifier = Modifier.width(Spacing.medium))
                            Column {
                                Text(
                                    text = plugin.name,
                                    color = VedraTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Trigger: \"${plugin.triggerWord}\"",
                                    color = VedraPurpleSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "URL: ${plugin.endpointUrl}",
                                    color = VedraTextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                dbService.deletePlugin(plugin.id)
                                refreshPlugins()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = VedraPinkAccent, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "UNIFIED DEVICE PERMISSIONS", color = VedraTextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                CustomButton(
                    text = "Grant All",
                    onClick = {
                        val missing = PermissionService.REQUIRED_PERMISSIONS.map { it.first }.toTypedArray()
                        permLauncher.launch(missing)
                    },
                    modifier = Modifier.height(28.dp),
                    isSecondary = true
                )
            }
        }

        items(permissionsList) { perm ->
            CustomCard(
                borderColor = if (perm.isGranted) Color(0xFF2E7D32) else Color(0xFFC62828)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (perm.isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (perm.isGranted) Color(0xFF81C784) else Color(0xFFE57373),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(Spacing.medium))
                        Column {
                            val shortName = perm.permissionName.substringAfterLast(".")
                            Text(text = shortName, color = VedraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = perm.requiredForFeature, color = VedraTextMuted, fontSize = 11.sp)
                        }
                    }

                    CustomButton(
                        text = if (perm.isGranted) "Granted" else "Grant",
                        onClick = {
                            permLauncher.launch(arrayOf(perm.permissionName))
                        },
                        isSecondary = perm.isGranted,
                        modifier = Modifier.height(30.dp)
                    )
                }
            }
        }

        item {
            Text(text = "GENERAL SETTINGS", color = VedraTextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        val generalSettings = listOf(
            Triple("Voice & Speech", "Voice model, wake word, speech speed", Icons.Default.Mic),
            Triple("App Language", "Current: ${selectedLang.displayName}", Icons.Default.Language),
            Triple("Floating Assistant", "Overlay widget on all screens", Icons.Default.Widgets),
            Triple("Appearance", "Dark theme, cyan accents, animations", Icons.Default.Palette),
            Triple("AI & Memory", "Model response style & recall window", Icons.Default.Psychology),
            Triple("Privacy & Security", "Data permissions & offline mode", Icons.Default.Security)
        )

        items(generalSettings) { (title, sub, icon) ->
            CustomCard(
                onClick = {
                    if (title == "Voice & Speech") {
                        isVoiceSettingsModalOpen = true
                    } else if (title == "App Language") {
                        isLanguageModalOpen = true
                    }
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = icon, contentDescription = null, tint = VedraPurpleSecondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(Spacing.medium))
                        Column {
                            Text(text = title, color = VedraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = sub, color = VedraTextMuted, fontSize = 11.sp)
                        }
                    }
                    if (title == "Floating Assistant") {
                        Switch(
                            checked = floatingWidgetEnabled,
                            onCheckedChange = { floatingWidgetEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VedraTextPrimary,
                                checkedTrackColor = VedraPurplePrimary
                            )
                        )
                    } else {
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = VedraTextMuted)
                    }
                }
            }
        }
    }

    // Modal for Voice Settings
    CustomModal(
        visible = isVoiceSettingsModalOpen,
        title = "Voice & Speech Settings",
        onDismissRequest = { isVoiceSettingsModalOpen = false }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Wake Word (\"Ved\")", color = VedraTextPrimary, fontWeight = FontWeight.Medium)
                Switch(
                    checked = wakeWordEnabled,
                    onCheckedChange = { wakeWordEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = VedraTextPrimary,
                        checkedTrackColor = VedraPurplePrimary
                    )
                )
            }

            Column {
                Text(
                    text = "Speech Speed (${String.format("%.1f", speechSpeed)}x)",
                    color = VedraTextPrimary,
                    fontSize = 14.sp
                )
                Slider(
                    value = speechSpeed,
                    onValueChange = { speechSpeed = it },
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = VedraPurplePrimary,
                        activeTrackColor = VedraPurplePrimary,
                        inactiveTrackColor = VedraBorder
                    )
                )
            }

            CustomButton(
                text = "Save Voice Preferences",
                onClick = { isVoiceSettingsModalOpen = false },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Modal for Adding Custom Task Chain / Routine
    CustomModal(
        visible = isAddRoutineModalOpen,
        title = "Create Task Chain Routine",
        onDismissRequest = { isAddRoutineModalOpen = false }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            CustomInput(
                value = routineTriggerInput,
                onValueChange = { routineTriggerInput = it },
                placeholder = "Trigger Phrase (e.g. good morning)"
            )
            CustomInput(
                value = routineActionsInput,
                onValueChange = { routineActionsInput = it },
                placeholder = "Actions comma separated (e.g. Read Weather, Read Battery, Open WhatsApp)"
            )
            CustomButton(
                text = "Save Routine Chain",
                onClick = {
                    if (routineTriggerInput.isNotBlank() && routineActionsInput.isNotBlank()) {
                        val actionsList = routineActionsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val jsonArr = JSONArray()
                        actionsList.forEach { jsonArr.put(it) }

                        dbService.addOrUpdateRoutine(routineTriggerInput, jsonArr.toString())
                        refreshRoutines()
                        isAddRoutineModalOpen = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Modal for Language Selector
    CustomModal(
        visible = isLanguageModalOpen,
        title = "Select App Language",
        onDismissRequest = { isLanguageModalOpen = false }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
            TranslationService.SUPPORTED_LANGUAGES.forEach { lang ->
                val isSel = selectedLang.code == lang.code
                CustomCard(
                    onClick = {
                        selectedLang = lang
                        TranslationService.setTargetLanguage(lang.code)
                        dbService.addOrUpdateMemory("pref_language", lang.code)
                        isLanguageModalOpen = false
                    },
                    borderColor = if (isSel) VedraPurplePrimary else VedraBorder
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = lang.displayName,
                            color = if (isSel) VedraCyanAccent else VedraTextPrimary,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Text(text = lang.code.uppercase(), color = VedraTextMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // Modal for Custom API Plugin Integration
    CustomModal(
        visible = isAddPluginModalOpen,
        title = "Define Custom Webhook / API",
        onDismissRequest = { isAddPluginModalOpen = false }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            Text(
                text = "Connect VEDRA to external HTTP APIs & webhooks. Trigger custom fetches using voice or text words.",
                color = VedraTextSecondary,
                fontSize = 12.sp
            )

            CustomInput(
                value = pluginNameInput,
                onValueChange = { pluginNameInput = it },
                placeholder = "Plugin Name (e.g., IoT Temperature Sensor)"
            )

            CustomInput(
                value = pluginUrlInput,
                onValueChange = { pluginUrlInput = it },
                placeholder = "Endpoint URL (e.g., https://api.example.com/v1/status)"
            )

            CustomInput(
                value = pluginTriggerInput,
                onValueChange = { pluginTriggerInput = it },
                placeholder = "Trigger Word (e.g., iot status)"
            )

            CustomInput(
                value = pluginHeadersInput,
                onValueChange = { pluginHeadersInput = it },
                placeholder = "Headers JSON (e.g., {\"Authorization\": \"Bearer key\"})"
            )

            CustomButton(
                text = "Save Plugin Integration",
                onClick = {
                    if (pluginNameInput.isNotBlank() && pluginUrlInput.isNotBlank() && pluginTriggerInput.isNotBlank()) {
                        dbService.addPlugin(
                            name = pluginNameInput,
                            endpointUrl = pluginUrlInput,
                            headersJson = pluginHeadersInput,
                            triggerWord = pluginTriggerInput
                        )
                        refreshPlugins()
                        isAddPluginModalOpen = false
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Clear Confirmation Modal
    CustomModal(
        visible = clearConfirmSection != null,
        title = "Confirm Delete ${clearConfirmSection ?: ""}",
        onDismissRequest = { clearConfirmSection = null }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
            Text(
                text = "Are you sure you want to permanently delete all records in ${clearConfirmSection ?: ""}? This action cannot be undone.",
                color = VedraTextSecondary,
                fontSize = 13.sp
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CustomButton(
                    text = "Cancel",
                    onClick = { clearConfirmSection = null },
                    isSecondary = true,
                    modifier = Modifier.weight(1f)
                )
                CustomButton(
                    text = "Delete All",
                    onClick = {
                        when (clearConfirmSection) {
                            "Notes" -> dbService.clearNotes()
                            "Flashcards" -> dbService.clearFlashcards()
                            "Habits" -> dbService.clearStudyHabits()
                            "Expenses" -> dbService.clearExpenses()
                            "Memories" -> dbService.clearMemories()
                            "Aliases" -> dbService.clearAliases()
                            "Shortcuts" -> dbService.clearAppMappings()
                        }
                        clearConfirmSection = null
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
