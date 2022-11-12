package com.bcc.exporeal.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bcc.exporeal.model.CategoryModel
import com.bcc.exporeal.ui.style.AppColor

@Composable
fun CategoryTag(
    categoryModel: CategoryModel
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Int.MAX_VALUE.dp))
            .border(
                color = AppColor.Blue60,
                shape = RoundedCornerShape(Int.MAX_VALUE.dp),
                width = 1.dp
            )
            .background(
                color = AppColor.Blue10
            )
            .padding(
                vertical = 8.dp,
                horizontal = 12.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        AppText(
            text = "${categoryModel.category_name}",
            textType = TextType.Body3,
            color = AppColor.Blue60
        )
    }
}

@Composable
fun ChatItemTag(
    word:String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Int.MAX_VALUE.dp))
            .border(
                color = AppColor.Blue60,
                shape = RoundedCornerShape(Int.MAX_VALUE.dp),
                width = 1.dp
            )
            .background(
                color = AppColor.Blue10
            )
            .padding(
                vertical = 4.dp,
                horizontal = 6.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        AppText(
            text = word,
            textType = TextType.Body3,
            color = AppColor.Blue60
        )
    }
}