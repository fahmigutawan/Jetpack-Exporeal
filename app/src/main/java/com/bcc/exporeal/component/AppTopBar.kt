package com.bcc.exporeal.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bcc.exporeal.ui.style.AppColor

@Composable
fun AppTopBar(
    onBackClicked: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null,
    title: String
) {
    TopAppBar(backgroundColor = AppColor.Neutral10) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    modifier = Modifier.clickable { onBackClicked() },
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )

                trailingContent?.let {
                    it()
                }
            }

            AppText(text = title, textType = TextType.H2)
        }
    }
}