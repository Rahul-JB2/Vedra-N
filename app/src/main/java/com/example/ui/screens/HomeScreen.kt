package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.services.BatteryStatus
import com.example.services.StorageDetails
import com.example.services.StorageWeatherService
import com.example.services.WeatherInfo
import com.example.ui.components.CustomButton
import com.example.ui.components.CustomCard
import com.example.ui.theme.Spacing
import com.example.ui.theme.VedraBackground
import com.example.ui.theme.VedraBlueAccent
import com.example.ui.theme.VedraBorder
import com.example.ui.theme.VedraCyanAccent
import com.example.ui.theme.VedraOnlineGreen
import com.example.ui.theme.VedraPinkAccent
import com.example.ui.theme.VedraPurplePrimary
import com.example.ui.theme.VedraPurpleSecondary
import com.example.ui.theme.VedraSurface
import com.example.ui.theme.VedraSurfaceVariant
import com.example.ui.theme.VedraTextMuted
import com.example.ui.theme.VedraTextPrimary
import com.example.ui.theme.VedraTextSecondary

import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.collectAsState
import com.example.services.ExternalService
import com.example.services.NotificationService
import com.example.services.UtilityService

import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VolumeUp
import com.example.services.DatabaseService
import com.example.services.VoiceService
import com.example.ui.components.CustomModal

@Composable
fun HomeScreen(
    dbService: DatabaseService,
    voiceService: VoiceService,
    onActivateVoice: () -> Unit,
    onNavigateTab: (Int) -> Unit,
    onExecuteQuickAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var battery by remember { mutableStateOf(BatteryStatus(88, "Discharging", false)) }
    var weather by remember { mutableStateOf(WeatherInfo()) }
    var storage by remember { mutableStateOf(StorageDetails(1.25, 42.5, 64.0)) }
    var storageStatusMsg by remember { mutableStateOf<String?>(null) }
    var isQrScannerOpen by remember { mutableStateOf(false) }
    var scannedResult by remember { mutableStateOf<String?>(null) }

    var isBriefingModalOpen by remember { mutableStateOf(false) }
    var dailyBriefingText by remember { mutableStateOf("") }

    val activeTimers = NotificationService.activeTimers.collectAsState().value

    fun refreshDashboardData() {
        battery = StorageWeatherService.getBatteryStatus(context)
        weather = StorageWeatherService.getWeatherInfo()
        storage = StorageWeatherService.getStorageDetails(context)
    }

    LaunchedEffect(Unit) {
        refreshDashboardData()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VedraBackground)
            .padding(horizontal = Spacing.medium),
        contentPadding = PaddingValues(vertical = Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Daily Briefing",
                            color = VedraTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "👋", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "VEDRA AI • All systems active",
                        color = VedraTextSecondary,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(VedraSurface)
                            .border(1.dp, VedraBorder, RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(VedraOnlineGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Online",
                                color = VedraTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(VedraSurface)
                            .border(1.dp, VedraBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = VedraTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Central Voice Orb Card
        item {
            VoiceOrbCard(
                onActivateVoice = onActivateVoice,
                onPlayBriefing = {
                    dailyBriefingText = NotificationService.generateDailyBriefing(context, dbService)
                    voiceService.speak(dailyBriefingText)
                    isBriefingModalOpen = true
                }
            )
        }

        // Active Timers & Alarms
        if (activeTimers.isNotEmpty()) {
            item {
                CustomCard(borderColor = VedraPurplePrimary) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = VedraPurplePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Active Timers & Alarms",
                                    color = VedraTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(Spacing.small))
                        activeTimers.forEach { timer ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = timer.title,
                                    color = VedraTextSecondary,
                                    fontSize = 13.sp
                                )
                                val mins = timer.remainingSeconds / 60
                                val secs = timer.remainingSeconds % 60
                                Text(
                                    text = String.format("%02d:%02d", mins, secs),
                                    color = VedraCyanAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Weather Widget Card
        item {
            CustomCard(borderColor = VedraCyanAccent) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(VedraCyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = null,
                                tint = VedraCyanAccent,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(Spacing.medium))
                        Column {
                            Text(
                                text = "${weather.temperature} • ${weather.condition}",
                                color = VedraTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "${weather.location} • Humidity: ${weather.humidity}",
                                color = VedraTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Storage & Battery Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                StatusCard(
                    title = "Battery Level",
                    value = "${battery.percentage}%",
                    status = battery.statusText,
                    icon = if (battery.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                    iconColor = VedraOnlineGreen,
                    modifier = Modifier.weight(1f)
                )
                StatusCard(
                    title = "Device Storage",
                    value = "${storage.freeSpaceGB} GB free",
                    status = "Total: ${storage.totalSpaceGB} GB",
                    icon = Icons.Default.Storage,
                    iconColor = VedraBlueAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Monthly Expense Summary Card
        item {
            val monthlyTotal = dbService.getMonthlyExpenseTotal()
            val expenses = dbService.getAllExpenses()

            CustomCard(borderColor = VedraPinkAccent) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "💳 MONTHLY EXPENSES",
                                color = VedraPinkAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "Total: ₹${String.format("%.2f", monthlyTotal)}",
                            color = VedraTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    if (expenses.isEmpty()) {
                        Text(
                            text = "No expenses logged this month. Say \"Spent 200 on books\" or \"Spent 50 on lunch\" to log.",
                            color = VedraTextMuted,
                            fontSize = 12.sp
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            expenses.take(3).forEach { exp ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "${exp.category}: ${exp.note}", color = VedraTextSecondary, fontSize = 12.sp)
                                    Text(text = "₹${exp.amount}", color = VedraPinkAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Storage Manager Section
        item {
            CustomCard(borderColor = VedraPurplePrimary) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = null,
                                tint = VedraPurpleSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(Spacing.small))
                            Column {
                                Text(
                                    text = "Storage Manager",
                                    color = VedraTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "App Cache: ${storage.cacheSizeMB} MB",
                                    color = VedraTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        CustomButton(
                            text = "Clear Cache",
                            onClick = {
                                val msg = StorageWeatherService.clearAppCache(context)
                                storageStatusMsg = msg
                                refreshDashboardData()
                            },
                            isSecondary = true,
                            modifier = Modifier.height(34.dp)
                        )
                    }

                    if (storageStatusMsg != null) {
                        Text(
                            text = storageStatusMsg!!,
                            color = VedraOnlineGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Suggestions Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VEDRA Quick Suggestions",
                    color = VedraTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "View all >",
                    color = VedraPurplePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateTab(3) } // Actions tab
                )
            }
        }

        // Suggestions Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                SuggestionCard(
                    title = "Open WhatsApp",
                    subtitle = "Quick launch",
                    color = Color(0xFF25D366),
                    modifier = Modifier.weight(1f),
                    onClick = { onExecuteQuickAction("open whatsapp") }
                )
                SuggestionCard(
                    title = "JEE Study Hub",
                    subtitle = "Planner & Flashcards",
                    color = VedraBlueAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(1) } // Study tab
                )
            }
            Spacer(modifier = Modifier.height(Spacing.small))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                SuggestionCard(
                    title = "Scan QR Code",
                    subtitle = "Barcode & Camera",
                    color = VedraCyanAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { isQrScannerOpen = true }
                )
                SuggestionCard(
                    title = "Call Mom",
                    subtitle = "Quick call",
                    color = VedraPinkAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onExecuteQuickAction("call mom") }
                )
            }
        }
    }

    // QR / Barcode Scanner Modal Component
    CustomModal(
        visible = isQrScannerOpen,
        title = "QR & Barcode Scanner",
        onDismissRequest = {
            isQrScannerOpen = false
            scannedResult = null
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .border(2.dp, VedraCyanAccent, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scanner Frame",
                    tint = VedraCyanAccent,
                    modifier = Modifier.size(64.dp)
                )
            }

            if (scannedResult == null) {
                Text(
                    text = "Point camera at QR code or Barcode",
                    color = VedraTextSecondary,
                    fontSize = 13.sp
                )
                CustomButton(
                    text = "Simulate Scan Result",
                    icon = Icons.Default.QrCodeScanner,
                    onClick = {
                        scannedResult = "https://vedra-assistant.ai/study/physics-notes-2026"
                    },
                    modifier = Modifier.height(36.dp)
                )
            } else {
                CustomCard(borderColor = VedraOnlineGreen) {
                    Column {
                        Text(text = "Scanned Code Result:", color = VedraTextMuted, fontSize = 11.sp)
                        Text(text = scannedResult!!, color = VedraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                    CustomButton(
                        text = "Copy Result",
                        onClick = {
                            UtilityService.writeToClipboard(context, scannedResult!!)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    CustomButton(
                        text = "Open Web",
                        onClick = {
                            ExternalService.searchWeb(context, scannedResult!!)
                        },
                        isSecondary = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    CustomModal(
        visible = isBriefingModalOpen,
        title = "VEDRA Daily Briefing",
        onDismissRequest = { isBriefingModalOpen = false }
    ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                Text(
                    text = dailyBriefingText,
                    color = VedraTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    CustomButton(
                        text = "Replay Audio",
                        icon = Icons.Default.VolumeUp,
                        onClick = { voiceService.speak(dailyBriefingText) },
                        modifier = Modifier.weight(1f)
                    )
                    CustomButton(
                        text = "Close",
                        onClick = { isBriefingModalOpen = false },
                        isSecondary = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
}

@Composable
fun VoiceOrbCard(
    onActivateVoice: () -> Unit,
    onPlayBriefing: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    CustomCard(
        onClick = onActivateVoice,
        testTag = "voice_orb_card"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    VedraPurplePrimary.copy(alpha = 0.5f),
                                    VedraCyanAccent.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(VedraPurplePrimary, VedraPurpleSecondary, VedraCyanAccent)
                            )
                        )
                        .border(2.dp, VedraTextPrimary.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Assistant",
                        tint = VedraTextPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            Text(
                text = "Tap to Talk to VEDRA",
                color = VedraPurpleSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomButton(
                    text = "Start Listening",
                    icon = Icons.Default.Mic,
                    onClick = onActivateVoice,
                    modifier = Modifier.height(36.dp)
                )

                CustomButton(
                    text = "Daily Briefing",
                    icon = Icons.Default.VolumeUp,
                    onClick = onPlayBriefing,
                    isSecondary = true,
                    modifier = Modifier.height(36.dp)
                )
            }
        }
    }
}

@Composable
fun SuggestionCard(
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomCard(
        modifier = modifier,
        onClick = onClick,
        testTag = "suggestion_card_$title"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = VedraTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = VedraTextMuted,
                    fontSize = 11.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun StatusCard(
    title: String,
    value: String,
    status: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    CustomCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.small))
            Column {
                Text(text = title, color = VedraTextMuted, fontSize = 11.sp)
                Text(text = value, color = VedraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = status, color = iconColor, fontSize = 10.sp)
            }
        }
    }
}
