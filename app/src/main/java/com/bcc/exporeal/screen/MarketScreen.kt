package com.bcc.exporeal.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bcc.exporeal.component.*
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.PagingState
import com.bcc.exporeal.viewmodel.MarketViewModel

@Composable
fun MarketScreen(
    navController: NavController
) {
    /**Attrs*/
    val viewModel = hiltViewModel<MarketViewModel>()
    val productLazyState = rememberLazyGridState()
    val permintaanLazyState = rememberLazyListState()

    /**Function*/

    /**Content*/
    MarketContent(
        navController = navController,
        viewModel = viewModel,
        productLazyState = productLazyState,
        permintaanLazyState = permintaanLazyState
    )
}

@Composable
private fun MarketContent(
    navController: NavController,
    viewModel: MarketViewModel,
    productLazyState: LazyGridState,
    permintaanLazyState: LazyListState
) {
    val topMenuWidth = LocalConfiguration.current.screenWidthDp / MarketTopMenuItem.values().size
    val gridItemWidth = LocalConfiguration.current.screenWidthDp / 2
    val queryNextProduct = remember {
        derivedStateOf {
            viewModel.productList.isNotEmpty()
                    && viewModel.productList.size % 8 == 0
                    && productLazyState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == viewModel.productList.size - 1
        }
    }
    val queryNextPermintaan = remember {
        derivedStateOf {
            viewModel.permintaanList.isNotEmpty()
                    && viewModel.permintaanList.size % 8 == 0
                    && permintaanLazyState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == viewModel.permintaanList.size - 1
        }
    }

    LaunchedEffect(key1 = queryNextProduct.value) {
        if (queryNextProduct.value) {
            viewModel.loadNextProducts()
        }
    }
    LaunchedEffect(key1 = queryNextPermintaan.value) {
        if (queryNextPermintaan.value) {
            viewModel.loadNextPermintaan()
        }
    }

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
        when (viewModel.selectedTopMenu.value) {
            MarketTopMenuItem.Produk -> {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Search
                    AppTextInputField(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
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

                    // Items
                    LazyVerticalGrid(
                        modifier = Modifier.padding(vertical = 16.dp),
                        state = productLazyState,
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        items(viewModel.productList) { item ->
                            Box(
                                modifier = Modifier
                                    .width(gridItemWidth.dp)
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ProductItem(
                                    productModel = item,
                                    onClick = {
                                        /*TODO*/
                                    }
                                )
                            }
                        }

                        if (viewModel.productPagingState.value == PagingState.NextLoad) {
                            items(8) {
                                Box(
                                    modifier = Modifier
                                        .width(gridItemWidth.dp)
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ProductItemLoading()
                                }
                            }
                        }
                    }
                }
            }
            MarketTopMenuItem.Permintaan -> {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    // Make request btn & Search
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Request
                        AppButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            onClick = { /*TODO*/ },
                            text = "MAKE A REQUEST"
                        )

                        // Search
                        AppTextInputField(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
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

                    // Items
                    LazyColumn(
                        state = permintaanLazyState,
                        modifier = Modifier.padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(viewModel.permintaanList){ item ->
                            PermintaanItem(
                                onDetailClicked = { /*TODO*/ },
                                permintaanModel = item,
                                userInfo = ,
                                category =
                            )
                        }
                    }
                }
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