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

@Composable
fun HomeScreen(
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
            VoiceOrbCard(onActivateVoice = onActivateVoice)
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
                    title = "Flashlight",
                    subtitle = "Toggle light",
                    color = VedraPurplePrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { onExecuteQuickAction("turn on flashlight") }
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
}

@Composable
fun VoiceOrbCard(onActivateVoice: () -> Unit) {
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

            CustomButton(
                text = "Start Listening",
                icon = Icons.Default.Mic,
                onClick = onActivateVoice,
                modifier = Modifier.height(36.dp)
            )
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
