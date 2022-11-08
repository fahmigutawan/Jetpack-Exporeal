package com.bcc.exporeal.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bcc.exporeal.component.AppText
import com.bcc.exporeal.component.AppTextInputField
import com.bcc.exporeal.component.TextType
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.viewmodel.MarketViewModel

@Composable
fun MarketScreen(
    navController: NavController
) {
    /**Attrs*/
    val viewModel = hiltViewModel<MarketViewModel>()

    /**Function*/

    /**Content*/
    MarketContent(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
private fun MarketContent(
    navController: NavController,
    viewModel: MarketViewModel
) {
    val topMenuWidth = LocalConfiguration.current.screenWidthDp / MarketTopMenuItem.values().size

    Column(modifier = Modifier.fillMaxSize()) {
        // Produk & Permintaan btn
        TopAppBar(backgroundColor = AppColor.Neutral10) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                MarketTopMenuItem.values().forEach { item ->
                    Box(
                        modifier = Modifier
                            .width(topMenuWidth.dp)
                            .fillMaxHeight()
                            .clickable { viewModel.selectedTopMenu.value = item },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(topMenuWidth.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AppText(
                                text = item.word,
                                textType = TextType.H3,
                                color = when (viewModel.selectedTopMenu.value) {
                                    item -> AppColor.Blue60
                                    else -> AppColor.Neutral100
                                }
                            )
                        }

                        if (viewModel.selectedTopMenu.value == item) {
                            Box(
                                modifier = Modifier
                                    .height(2.dp)
                                    .width(topMenuWidth.dp)
                                    .background(AppColor.Blue60)
                            )
                        }
                    }
                }
            }
        }

        // Content
        LazyColumn{
            // Search Field
            item {
                AppTextInputField(
                    placeHolderText = "Cari di Exporeal",
                    valueState = viewModel.searchState,
                    endContent = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Icon",
                            tint = AppColor.Neutral60
                        )
                    }
                )
            }
        }
    }
}

enum class MarketTopMenuItem(
    val word: String
) {
    Produk(
        "Produk"
    ),
    Permintaan(
        "Permintaan"
    )
}