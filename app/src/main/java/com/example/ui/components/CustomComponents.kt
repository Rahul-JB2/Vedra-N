package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.Spacing
import com.example.ui.theme.VedraBorder
import com.example.ui.theme.VedraPurplePrimary
import com.example.ui.theme.VedraPurpleSecondary
import com.example.ui.theme.VedraSurface
import com.example.ui.theme.VedraSurfaceVariant
import com.example.ui.theme.VedraTextMuted
import com.example.ui.theme.VedraTextPrimary
import com.example.ui.theme.VedraTextSecondary

/**
 * Reusable CustomButton with VEDRA gradient theme styling.
 */
@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    isSecondary: Boolean = false,
    testTag: String = "custom_button"
) {
    val backgroundBrush = if (isSecondary) {
        Brush.linearGradient(listOf(VedraSurfaceVariant, VedraSurfaceVariant))
    } else {
        Brush.linearGradient(listOf(VedraPurplePrimary, VedraPurpleSecondary))
    }

    val shape = RoundedCornerShape(Spacing.buttonCorner)

    Box(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 48.dp)
            .clip(shape)
            .background(backgroundBrush)
            .border(
                width = if (isSecondary) 1.dp else 0.dp,
                color = if (isSecondary) VedraBorder else Color.Transparent,
                shape = shape
            )
            .clickable(enabled = enabled && !isLoading) { onClick() }
            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = VedraTextPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(Spacing.small))
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = VedraTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.small))
            }
            Text(
                text = text,
                color = if (enabled) VedraTextPrimary else VedraTextMuted,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}

/**
 * Reusable CustomCard with dark surface and subtle border glow.
 */
@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cornerShape: RoundedCornerShape = RoundedCornerShape(Spacing.cardCorner),
    borderColor: Color = VedraBorder,
    testTag: String = "custom_card",
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = cornerShape,
        colors = CardDefaults.cardColors(
            containerColor = VedraSurface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Box(modifier = Modifier.padding(Spacing.medium)) {
            content()
        }
    }
}

/**
 * Reusable CustomInput field for text entry across VEDRA screens.
 */
@Composable
fun CustomInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Type a command...",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onSend: (() -> Unit)? = null,
    testTag: String = "custom_input"
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = VedraTextMuted,
                fontSize = 14.sp
            )
        },
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = VedraTextSecondary
                )
            }
        } else null,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = VedraSurface,
            unfocusedContainerColor = VedraSurface,
            focusedBorderColor = VedraPurplePrimary,
            unfocusedBorderColor = VedraBorder,
            focusedTextColor = VedraTextPrimary,
            unfocusedTextColor = VedraTextPrimary,
            cursorColor = VedraPurplePrimary
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = if (onSend != null) ImeAction.Send else ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onSend = { onSend?.invoke() }
        )
    )
}

/**
 * Reusable CustomModal dialog component for modals/alerts/actions.
 */
@Composable
fun CustomModal(
    visible: Boolean,
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "custom_modal",
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Dialog(onDismissRequest = onDismissRequest) {
            Surface(
                modifier = modifier
                    .testTag(testTag)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Spacing.cardCorner))
                    .border(1.dp, VedraBorder, RoundedCornerShape(Spacing.cardCorner)),
                color = VedraSurface
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.medium)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            color = VedraTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        IconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = VedraTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.small))

                    content()
                }
            }
        }
    }
}

/**
 * Reusable CustomList component for rendered vertical lists with empty state support.
 */
@Composable
fun <T> CustomList(
    items: List<T>,
    modifier: Modifier = Modifier,
    emptyText: String = "No items available",
    itemKey: ((T) -> Any)? = null,
    itemContent: @Composable (T) -> Unit
) {
    if (items.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(Spacing.large),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyText,
                color = VedraTextMuted,
                fontSize = 14.sp
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(vertical = Spacing.small),
            verticalArrangement = Arrangement.spacedBy(Spacing.small)
        ) {
            items(
                items = items,
                key = itemKey
            ) { item ->
                itemContent(item)
            }
        }
    }
}
