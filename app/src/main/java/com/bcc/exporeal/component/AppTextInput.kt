package com.bcc.exporeal.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.sp
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.ui.style.AppType

@Composable
fun AppTextInputField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    textPadding: Dp = 12.dp,
    placeHolderText: String,
    placeHolderColor: Color = AppColor.Neutral50,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = AppType.body3(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if(singleLine) 1 else Int.MAX_VALUE,
    leadingContent: @Composable (() -> Unit)? = null,
    endContent: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    valueState: MutableState<String>,
    backgroundColor: Color = AppColor.Neutral10
) {
    val containerHeight = remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .heightIn(min = containerHeight.value)
            .border(width = 1.dp, color = AppColor.Neutral50, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.TopEnd,
    ) {
        BasicTextField(
            value = valueState.value,
            onValueChange = { valueState.value = it },
            modifier = Modifier
                .padding(textPadding)
                .fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = visualTransformation
        ) { field ->
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
                    textType = TextType.Body3,
                    color = placeHolderColor
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
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
            if (leadingContent != null) {
                leadingContent()
            } else Box(modifier = Modifier)

            if (endContent != null) {
                endContent()
            } else Box(modifier = Modifier)
        }
    }
}