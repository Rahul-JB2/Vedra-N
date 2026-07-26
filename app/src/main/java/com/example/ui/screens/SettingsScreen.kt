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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CustomButton
import com.example.ui.components.CustomCard
import com.example.ui.components.CustomModal
import com.example.ui.theme.Spacing
import com.example.ui.theme.VedraBackground
import com.example.ui.theme.VedraBorder
import com.example.ui.theme.VedraCyanAccent
import com.example.ui.theme.VedraPurplePrimary
import com.example.ui.theme.VedraPurpleSecondary
import com.example.ui.theme.VedraTextMuted
import com.example.ui.theme.VedraTextPrimary
import com.example.ui.theme.VedraTextSecondary

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    var isVoiceSettingsModalOpen by remember { mutableStateOf(false) }
    var speechSpeed by remember { mutableFloatStateOf(1.0f) }
    var wakeWordEnabled by remember { mutableStateOf(true) }

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
                text = "SETTINGS",
                color = VedraTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Customize VEDRA your way",
                color = VedraTextSecondary,
                fontSize = 12.sp
            )
        }

        // Vedra Pro Card
        item {
            CustomCard(
                borderColor = VedraPurplePrimary
            ) {
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
                            text = "Access to offline AI, voice assistant & custom triggers.",
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

        item {
            Text(text = "GENERAL", color = VedraTextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        val generalSettings = listOf(
            Triple("Voice & Speech", "Voice model, wake word, speech speed", Icons.Default.Mic),
            Triple("Appearance", "Dark theme, cyan accents, animations", Icons.Default.Palette),
            Triple("AI & Memory", "Model response style & recall window", Icons.Default.Psychology),
            Triple("Privacy & Security", "Data permissions & offline mode", Icons.Default.Security)
        )

        items(generalSettings) { (title, sub, icon) ->
            CustomCard(
                onClick = {
                    if (title == "Voice & Speech") {
                        isVoiceSettingsModalOpen = true
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
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = VedraTextMuted)
                }
            }
        }
    }

    // Modal using CustomModal
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
}
