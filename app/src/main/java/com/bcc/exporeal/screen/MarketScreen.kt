package com.bcc.exporeal.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
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
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.PagingState
import com.bcc.exporeal.util.Resource
import com.bcc.exporeal.viewmodel.MainViewModel
import com.bcc.exporeal.viewmodel.MarketViewModel

@Composable
fun MarketScreen(
    navController: NavController,
    mainViewModel: MainViewModel
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
        mainViewModel = mainViewModel,
        productLazyState = productLazyState,
        permintaanLazyState = permintaanLazyState
    )
}

@Composable
private fun MarketContent(
    navController: NavController,
    viewModel: MarketViewModel,
    mainViewModel:MainViewModel,
    productLazyState: LazyGridState,
    permintaanLazyState: LazyListState
) {
    val topMenuWidth = LocalConfiguration.current.screenWidthDp / MarketTopMenuItem.values().size
    val gridItemWidth = LocalConfiguration.current.screenWidthDp / 2
    val queryNextProduct = remember {
        derivedStateOf {
            viewModel.productList.isNotEmpty() && viewModel.productList.size % 8 == 0 && productLazyState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == viewModel.productList.size - 1
        }
    }
    val queryNextPermintaan = remember {
        derivedStateOf {
            viewModel.permintaanList.isNotEmpty() && viewModel.permintaanList.size % 8 == 0 && permintaanLazyState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == viewModel.permintaanList.size - 1
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
    LaunchedEffect(key1 = viewModel.permintaanPagingState.value == PagingState.Success) {
        if (viewModel.permintaanPagingState.value == PagingState.Success && viewModel.permintaanList.isNotEmpty()) {
            for (i in 0..((viewModel.permintaanList.size - viewModel.listOfUserInfo.size) - 1)) {
                viewModel.listOfUserInfo.add(Resource.Loading())
                viewModel.listOfCategory.add(Resource.Loading())
            }
        }
    }
    LaunchedEffect(key1 = viewModel.permintaanPagingState.value == PagingState.Success) {
        if (viewModel.permintaanPagingState.value == PagingState.Success && viewModel.permintaanList.isNotEmpty()) {
            viewModel.permintaanList.forEachIndexed { index, permintaanModel ->
                if (viewModel.listOfUserInfo[index] is Resource.Success) {

                } else {
                    viewModel.getUserById(uid = permintaanModel.peminta_uid ?: "", onSuccess = {
                        viewModel.listOfUserInfo[index] = Resource.Success(it)
                    }, onFailed = {
                        /*TODO*/
                    })
                }

                if (viewModel.listOfCategory[index] is Resource.Success) {

                } else {
                    viewModel.getCategoryById(category_id = permintaanModel.category_id ?: "",
                        onSuccess = {
                            viewModel.listOfCategory[index] = Resource.Success(it)
                        },
                        onFailed = {
                            /*TODO*/
                        })
                }
            }
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
                    // Items
                    LazyVerticalGrid(
                        modifier = Modifier.padding(vertical = 16.dp),
                        state = productLazyState,
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (viewModel.productList.isNotEmpty()) {
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
                                            mainViewModel.pickedProductToProductDetailScreen.value = it
                                            if (mainViewModel.pickedProductToProductDetailScreen.value != null) {
                                                navController.navigate(route = AppNavRoute.ProductDetailScreen.name)
                                            }
                                        }
                                    )
                                }
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
            MarketTopMenuItem.Permintaan -> {
                LazyColumn(
                    state = permintaanLazyState,
                    modifier = Modifier.padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    // Request Btn
                    item {
                        AppButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            onClick = { /*TODO*/ },
                            text = "MAKE A REQUEST"
                        )
                    }

                    // Items
                    when {
                        viewModel.permintaanList.isNotEmpty() -> {
                            if (viewModel.listOfUserInfo.size == viewModel.permintaanList.size
                                && viewModel.listOfCategory.size == viewModel.permintaanList.size
                            ) {
                                itemsIndexed(viewModel.permintaanList) { index, item ->
                                    PermintaanItem(
                                        onDetailClicked = {
                                            mainViewModel.pickedPermintaanToPermintaanDetailScreen.value = item
                                            if (mainViewModel.pickedPermintaanToPermintaanDetailScreen.value != null) {
                                                navController.navigate(route = AppNavRoute.PermintaanDetailScreen.name)
                                            }
                                        },
                                        permintaanModel = item,
                                        userInfo = viewModel.listOfUserInfo[index],
                                        category = viewModel.listOfCategory[index]
                                    )
                                }
                            }
                        }


                        else -> {
                            if (viewModel.productPagingState.value == PagingState.NextLoad) {
                                items(8) {
                                    PermintaanItemLoading()
                                }
                            }
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