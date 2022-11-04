package com.bcc.exporeal.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.bcc.exporeal.component.TextType.*
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.ui.style.AppType

@Composable
fun AppText(
    modifier: Modifier = Modifier,
    text: String,
    textType: TextType,
    color: Color = AppColor.Neutral100
) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = when (textType) {
            H1 -> AppType.h1()
            H2 -> AppType.h2()
            H3 -> AppType.h3()
            H4 -> AppType.h4()
            H5 -> AppType.h5()
            Body1 -> AppType.body1()
            Body2 -> AppType.body2()
            Body3 -> AppType.body3()
            Body1Semibold -> AppType.body1Semibold()
            Body2Semibold -> AppType.body2Semibold()
            Body3Semibold -> AppType.body3Semibold()
            FieldLabel -> AppType.fieldLabel()
            FieldPlaceholder -> AppType.fieldPlaceholder()
            BottomMenu -> AppType.bottomMenu()
            ButtonNormal -> AppType.buttonNormal()
            ButtonSmall -> AppType.buttonSmall()
        }
    )
}

@Composable
fun AppTextButton(
    modifier: Modifier = Modifier,
    text: String,
    textType: TextType,
    onClick: () -> Unit,
    color: Color = AppColor.Neutral100,
    rippleColor: Color = AppColor.Neutral100
) {
    val itemWidth = remember { mutableStateOf(0.dp) }
    val itemHeight = remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(contentAlignment = Alignment.Center) {
        Text(
            modifier = modifier.onSizeChanged {
                with(density) {
                    itemHeight.value = it.height.toDp()
                    itemWidth.value = it.width.toDp()
                }
            },
            text = text,
            color = color,
            style = when (textType) {
                H1 -> AppType.h1()
                H2 -> AppType.h2()
                H3 -> AppType.h3()
                H4 -> AppType.h4()
                H5 -> AppType.h5()
                Body1 -> AppType.body1()
                Body2 -> AppType.body2()
                Body3 -> AppType.body3()
                Body1Semibold -> AppType.body1Semibold()
                Body2Semibold -> AppType.body2Semibold()
                Body3Semibold -> AppType.body3Semibold()
                FieldLabel -> AppType.fieldLabel()
                FieldPlaceholder -> AppType.fieldPlaceholder()
                BottomMenu -> AppType.bottomMenu()
                ButtonNormal -> AppType.buttonNormal()
                ButtonSmall -> AppType.buttonSmall()
            }
        )

        Box(
            modifier = modifier
                .clip(RoundedCornerShape(4.dp))
                .size(width = itemWidth.value, height = itemHeight.value)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true, color = rippleColor),
                    onClick = onClick
                )
        )
    }
}

enum class TextType {
    H1,
    H2,
    H3,
    H4,
    H5,
    Body1,
    Body2,
    Body3,
    Body1Semibold,
    Body2Semibold,
    Body3Semibold,
    ButtonNormal,
    ButtonSmall,
    FieldLabel,
    FieldPlaceholder,
    BottomMenu
}