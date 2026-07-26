package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.services.DatabaseService
import com.example.services.UtilityService
import com.example.services.VoiceService
import com.example.ui.screens.ActionsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StudyHubScreen
import com.example.ui.screens.VedScreen
import com.example.ui.screens.VoiceModeOverlay
import com.example.ui.theme.VedraBackground
import com.example.ui.theme.VedraPurplePrimary
import com.example.ui.theme.VedraSurface
import com.example.ui.theme.VedraTextMuted
import com.example.ui.theme.VedraTheme
import kotlinx.coroutines.delay

import com.example.services.BackgroundService
import com.example.services.NotificationService

class MainActivity : ComponentActivity() {

    private lateinit var dbService: DatabaseService
    private lateinit var voiceService: VoiceService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dbService = DatabaseService(this)
        voiceService = VoiceService(this)

        NotificationService.createNotificationChannel(this)
        BackgroundService.startBackgroundTasks(this, dbService)

        setContent {
            VedraTheme {
                MainAppLayout(
                    dbService = dbService,
                    voiceService = voiceService
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceService.shutdown()
    }
}

data class TabItem(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainAppLayout(
    dbService: DatabaseService,
    voiceService: VoiceService
) {
    val context = LocalContext.current
    var isVoiceModeActive by remember { mutableStateOf(false) }
    var activeTab by remember { mutableIntStateOf(0) }
    var hasUserInteracted by remember { mutableStateOf(false) }

    // Phase 1, Step 3: Initial launch startup logic
    LaunchedEffect(Unit) {
        isVoiceModeActive = true
        delay(5000)
        if (!hasUserInteracted) {
            isVoiceModeActive = false
            activeTab = 0 // Home
        }
    }

    val tabs = listOf(
        TabItem("Home", Icons.Default.Home),
        TabItem("Study", Icons.Default.School),
        TabItem("Ved", Icons.Default.Mic),
        TabItem("Actions", Icons.Default.FlashOn),
        TabItem("Memory", Icons.Default.Psychology),
        TabItem("Settings", Icons.Default.Settings)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Row(
                modifier = Modifier
                    .testTag("bottom_navigation_bar")
                    .fillMaxWidth()
                    .background(VedraSurface)
                    .navigationBarsPadding()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = activeTab == index
                    val tint = if (isSelected) VedraPurplePrimary else VedraTextMuted

                    Box(
                        modifier = Modifier
                            .testTag("tab_${tab.title.lowercase()}")
                            .weight(1f)
                            .combinedClickable(
                                onClick = {
                                    hasUserInteracted = true
                                    activeTab = index
                                },
                                onLongClick = {
                                    hasUserInteracted = true
                                    if (index == 2) { // Ved tab long press -> Activates VoiceMode globally
                                        isVoiceModeActive = true
                                    }
                                }
                            )
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = tint,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tab.title,
                                color = tint,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(VedraBackground)
        ) {
            when (activeTab) {
                0 -> SafeTabBoundary("Home") {
                    HomeScreen(
                        dbService = dbService,
                        voiceService = voiceService,
                        onActivateVoice = {
                            hasUserInteracted = true
                            isVoiceModeActive = true
                        },
                        onNavigateTab = { tab ->
                            hasUserInteracted = true
                            activeTab = tab
                        },
                        onExecuteQuickAction = { actionText ->
                            hasUserInteracted = true
                            UtilityService.parseAndExecuteLocalCommand(context, dbService, actionText)
                        }
                    )
                }
                1 -> SafeTabBoundary("Study Hub") {
                    StudyHubScreen(
                        dbService = dbService
                    )
                }
                2 -> SafeTabBoundary("Ved AI Assistant") {
                    VedScreen(
                        dbService = dbService,
                        voiceService = voiceService,
                        onActivateVoiceMode = {
                            hasUserInteracted = true
                            isVoiceModeActive = true
                        }
                    )
                }
                3 -> SafeTabBoundary("Actions") {
                    ActionsScreen(
                        onExecuteAction = { cmd ->
                            hasUserInteracted = true
                            UtilityService.parseAndExecuteLocalCommand(context, dbService, cmd)
                        }
                    )
                }
                4 -> SafeTabBoundary("Memory & Notes") {
                    MemoryScreen(
                        dbService = dbService,
                        onTestLaunch = { customWord ->
                            hasUserInteracted = true
                            UtilityService.parseAndExecuteLocalCommand(context, dbService, "open $customWord")
                        }
                    )
                }
                5 -> SafeTabBoundary("Settings") {
                    SettingsScreen(
                        dbService = dbService
                    )
                }
            }

            // Global VoiceMode Overlay
            if (isVoiceModeActive) {
                VoiceModeOverlay(
                    voiceService = voiceService,
                    onClose = {
                        hasUserInteracted = true
                        isVoiceModeActive = false
                    }
                )
            } else {
                com.example.ui.components.FloatingAssistantWidget(
                    voiceService = voiceService,
                    dbService = dbService,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 24.dp, end = 16.dp)
                )
            }
        }
    }
}

@Composable
fun SafeTabBoundary(
    tabName: String,
    content: @Composable () -> Unit
) {
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (hasError) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            com.example.ui.components.CustomCard(borderColor = com.example.ui.theme.VedraPinkAccent) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "⚡ $tabName Screen Error",
                        color = com.example.ui.theme.VedraPinkAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (errorMessage.isNotBlank()) errorMessage else "An unexpected error occurred in $tabName.",
                        color = com.example.ui.theme.VedraTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    com.example.ui.components.CustomButton(
                        text = "Reload Screen",
                        onClick = {
                            hasError = false
                            errorMessage = ""
                        }
                    )
                }
            }
        }
    } else {
        content()
    }
}
