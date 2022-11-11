package com.bcc.exporeal.component

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.ui.style.AppType

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppDropdownField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    dropdownModifier: Modifier = Modifier,
    contentModifier: Modifier,
    onClick: () -> Unit,
    isExpanded: MutableState<Boolean>,
    onDismissRequest: () -> Unit,
    textPadding: Dp = 12.dp,
    placeHolderText: String,
    placeHolderColor: Color = AppColor.Neutral50,
    enabled: Boolean = true,
    readOnly: Boolean = true,
    textStyle: TextStyle = AppType.body2(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    leadingContent: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    valueState: MutableState<String>,
    backgroundColor: Color = AppColor.Neutral10,
    content: @Composable () -> Unit
) {
    val containerHeight = remember { mutableStateOf(0.dp) }
    val containerWidth = remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .heightIn(min = containerHeight.value)
            .border(width = 1.dp, color = AppColor.Neutral50, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.TopEnd,
    ) {
        Box(
            modifier = Modifier.onSizeChanged {
                with(density) {
                    if (it.height.toDp() > containerHeight.value) {
                        containerHeight.value = it.height.toDp()
                    }
                    containerWidth.value = it.width.toDp()
                }
            }
        ) {
            BasicTextField(
                value = valueState.value,
                onValueChange = { valueState.value = it },
                modifier = Modifier
                    .padding(textPadding),
                enabled = enabled,
                readOnly = readOnly,
                textStyle = textStyle,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                singleLine = singleLine,
                maxLines = maxLines,
                visualTransformation = visualTransformation
            ) { field ->
                Column {
                    Row(
                        modifier = contentModifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        leadingContent?.let {
                            Box {
                                it()
                            }
                        }

                        Box(
                            modifier = Modifier.onSizeChanged {
                                with(density) {
                                    containerHeight.value = it.height.toDp()
                                }
                            },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            field()
                            if (valueState.value.isEmpty()) AppText(
                                text = placeHolderText,
                                textType = TextType.Body2,
                                color = placeHolderColor
                            )
                        }
                    }

                    DropdownMenu(
                        modifier = dropdownModifier,
                        expanded = isExpanded.value,
                        onDismissRequest = onDismissRequest
                    ) {
                        AnimatedVisibility(visible = isExpanded.value) {
                            Column {
                                content()
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .width(containerWidth.value)
                .padding(textPadding)
                .onSizeChanged {
                    with(density) {
                        if (it.height.toDp() > containerHeight.value) {
                            containerHeight.value = it.height.toDp()
                        }
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier)

            AnimatedContent(targetState = isExpanded.value) { state ->
                if (state) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropUp,
                        contentDescription = "Arrow"
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Arrow"
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(
                    width = containerWidth.value,
                    height = containerHeight.value
                )
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    indication = rememberRipple(color = AppColor.Neutral100),
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick
                )
        )
    }
}