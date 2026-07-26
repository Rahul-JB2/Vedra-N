package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.services.DatabaseService
import com.example.services.UtilityService
import com.example.services.VoiceService
import com.example.ui.theme.Spacing
import com.example.ui.theme.VedraCyanAccent
import com.example.ui.theme.VedraPurplePrimary
import com.example.ui.theme.VedraSurface
import com.example.ui.theme.VedraTextMuted
import com.example.ui.theme.VedraTextPrimary
import com.example.ui.theme.VedraTextSecondary
import kotlin.math.roundToInt

@Composable
fun FloatingAssistantWidget(
    voiceService: VoiceService,
    dbService: DatabaseService,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var widgetResponseText by remember { mutableStateOf("TAP widget for quick micro-voice assistant or tools") }
    var noteInputModal by remember { mutableStateOf(false) }
    var quickNoteText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        if (!isExpanded) {
            // Collapsed Floating Orb
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(VedraPurplePrimary)
                    .border(2.dp, VedraCyanAccent, CircleShape)
                    .shadow(8.dp, CircleShape)
                    .clickable { isExpanded = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Floating VEDRA Assistant",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        } else {
            // Expanded Micro-Voice Overlay Sheet
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Box(
                    modifier = Modifier
                        .width(320.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(VedraSurface)
                        .border(1.dp, VedraPurplePrimary, RoundedCornerShape(20.dp))
                        .padding(Spacing.medium)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = VedraCyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "VEDRA Micro Assistant",
                                    color = VedraTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            IconButton(onClick = { isExpanded = false }, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = VedraTextMuted
                                )
                            }
                        }

                        // Response / Speech display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E1E2E))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = widgetResponseText,
                                color = VedraTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }

                        // Mic Trigger
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (voiceService.isListening.value) Color(0xFFE53935) else VedraPurplePrimary)
                                    .clickable {
                                        if (voiceService.isListening.value) {
                                            voiceService.stopListening()
                                        } else {
                                            voiceService.startListening(
                                                onResult = { query ->
                                                    val res = UtilityService.parseAndExecuteLocalCommand(context, dbService, query)
                                                    widgetResponseText = res.responseMessage
                                                    voiceService.speak(res.responseMessage)
                                                },
                                                onError = { err ->
                                                    widgetResponseText = err
                                                }
                                            )
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Speak",
                                    tint = Color.White
                                )
                            }
                        }

                        Text(
                            text = "1-TAP QUICK ACTIONS",
                            color = VedraTextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )

                        // 4 Quick Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            QuickActionButton(
                                icon = Icons.Default.FlashOn,
                                label = "Torch",
                                onClick = {
                                    val res = UtilityService.parseAndExecuteLocalCommand(context, dbService, "turn on flashlight")
                                    widgetResponseText = res.responseMessage
                                }
                            )
                            QuickActionButton(
                                icon = Icons.Default.EditNote,
                                label = "Note",
                                onClick = {
                                    noteInputModal = true
                                }
                            )
                            QuickActionButton(
                                icon = Icons.Default.Calculate,
                                label = "Calc",
                                onClick = {
                                    val res = UtilityService.parseAndExecuteLocalCommand(context, dbService, "calculator 25 * 4")
                                    widgetResponseText = res.responseMessage
                                }
                            )
                            QuickActionButton(
                                icon = Icons.Default.Apps,
                                label = "Apps",
                                onClick = {
                                    val res = UtilityService.parseAndExecuteLocalCommand(context, dbService, "open youtube")
                                    widgetResponseText = res.responseMessage
                                }
                            )
                        }

                        if (noteInputModal) {
                            CustomInput(
                                value = quickNoteText,
                                onValueChange = { quickNoteText = it },
                                placeholder = "Type quick note & tap save..."
                            )
                            CustomButton(
                                text = "Save Note to SQLite",
                                onClick = {
                                    if (quickNoteText.isNotBlank()) {
                                        dbService.addOrUpdateMemory("Quick Note", quickNoteText)
                                        widgetResponseText = "Saved quick note: \"$quickNoteText\""
                                        quickNoteText = ""
                                        noteInputModal = false
                                    }
                                },
                                modifier = Modifier.height(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF2B293D)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = VedraCyanAccent,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = VedraTextPrimary, fontSize = 10.sp)
    }
}
