package com.bcc.exporeal.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bcc.exporeal.ui.style.AppColor

@Composable
fun AppWarningText(
    modifier: Modifier = Modifier, text: String
) {
    Box(modifier = modifier.background(AppColor.Warning20)) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Alert",
                tint = AppColor.Warning100
            )

            AppText(text = text, textType = TextType.Body3)
        }
    }
}