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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                            text = "Good Morning, User",
                            color = VedraTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "👋", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "I'm Vedra, your AI assistant",
                        color = VedraTextSecondary,
                        fontSize = 14.sp
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

        // Central Glowing Voice Orb
        item {
            VoiceOrbCard(onActivateVoice = onActivateVoice)
        }

        // VEDRA Suggestions Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VEDRA Suggestions for you",
                    color = VedraTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "View all >",
                    color = VedraPurplePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onNavigateTab(2) }
                )
            }
        }

        // Suggestions Grid (4 cards)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                SuggestionCard(
                    title = "Open WhatsApp",
                    subtitle = "Open directly",
                    color = Color(0xFF25D366),
                    modifier = Modifier.weight(1f),
                    onClick = { onExecuteQuickAction("open whatsapp") }
                )
                SuggestionCard(
                    title = "Study Planner",
                    subtitle = "Plan your study",
                    color = VedraBlueAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onExecuteQuickAction("study planner") }
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
                    title = "Calculator",
                    subtitle = "Perform math",
                    color = VedraPinkAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onExecuteQuickAction("open calculator") }
                )
            }
        }

        // Study Hub Card
        item {
            CustomCard(
                borderColor = VedraPurplePrimary,
                testTag = "study_hub_card"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(VedraSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = VedraPurpleSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(Spacing.medium))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Study Mode",
                                    color = VedraTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "New",
                                    color = VedraCyanAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(
                                            VedraCyanAccent.copy(alpha = 0.2f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Your smart study companion for exams",
                                color = VedraTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    CustomButton(
                        text = "Open Hub",
                        onClick = { onNavigateTab(1) },
                        isSecondary = true
                    )
                }
            }
        }

        // Quick Status
        item {
            Text(
                text = "Quick Status",
                color = VedraTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = Spacing.small)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                StatusCard(
                    title = "Battery",
                    value = "85%",
                    status = "Discharging",
                    icon = Icons.Default.BatteryFull,
                    iconColor = VedraOnlineGreen,
                    modifier = Modifier.weight(1f)
                )
                StatusCard(
                    title = "Storage",
                    value = "64%",
                    status = "40.1 GB free",
                    icon = Icons.Default.Storage,
                    iconColor = VedraBlueAccent,
                    modifier = Modifier.weight(1f)
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
                modifier = Modifier.size(130.dp)
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(120.dp)
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

                // Orb container
                Box(
                    modifier = Modifier
                        .size(90.dp)
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
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.medium))

            Text(
                text = "Tap to Start Listening",
                color = VedraPurpleSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                CustomButton(
                    text = "Start",
                    icon = Icons.Default.Mic,
                    onClick = onActivateVoice,
                    modifier = Modifier.height(38.dp)
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
                    fontSize = 14.sp
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
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(Spacing.small))
            Column {
                Text(text = title, color = VedraTextMuted, fontSize = 11.sp)
                Text(text = value, color = VedraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = status, color = iconColor, fontSize = 10.sp)
            }
        }
    }
}
