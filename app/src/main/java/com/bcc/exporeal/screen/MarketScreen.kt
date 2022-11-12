package com.bcc.exporeal.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bcc.exporeal.component.*
import com.bcc.exporeal.model.CategoryModel
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.PagingState
import com.bcc.exporeal.util.Resource
import com.bcc.exporeal.viewmodel.MainViewModel
import com.bcc.exporeal.viewmodel.MarketViewModel

@Composable
fun MarketScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    tab: String = "produk",
    category_id: String = ""
) {
    /**Attrs*/
    val viewModel = hiltViewModel<MarketViewModel>()
    val productLazyState = rememberLazyGridState()
    val permintaanLazyState = rememberLazyListState()
    val category = viewModel.category.collectAsState()
    val listsAreEmpty = remember {
        derivedStateOf {
            viewModel.productList.isEmpty() && viewModel.permintaanList.isEmpty()
        }
    }

    /**Function*/
    if (viewModel.tabArgumentShouldProceeded.value) {
        LaunchedEffect(key1 = true) {
            when (tab) {
                "produk" -> {
                    viewModel.selectedTopMenu.value = MarketTopMenuItem.Produk
                }
                "permintaan" -> {
                    viewModel.selectedTopMenu.value = MarketTopMenuItem.Permintaan
                }
            }

            viewModel.tabArgumentShouldProceeded.value = false
        }
    }
    if (viewModel.categoryArgumentShouldProceeded.value && category_id.isNotEmpty()) {
        LaunchedEffect(key1 = true) {
            viewModel.getCategoryById(category_id = category_id)
        }
    }
    if (viewModel.shouldLoadFirstItems.value && listsAreEmpty.value) {
        LaunchedEffect(key1 = true) {
            when {
                category_id.isNotEmpty() -> {
                    viewModel.loadFirstProductsByCategory(category_id)
                    viewModel.loadFirstPermintaanByCategory(category_id)
                }

                else -> {
                    viewModel.loadFirstProducts()
                    viewModel.loadFirstPermintaan()
                }
            }
        }
    }

    /**Content*/
    MarketContent(
        navController = navController,
        viewModel = viewModel,
        mainViewModel = mainViewModel,
        productLazyState = productLazyState,
        permintaanLazyState = permintaanLazyState,
        category = category,
        category_id = category_id
    )
}

@Composable
private fun MarketContent(
    navController: NavController,
    viewModel: MarketViewModel,
    mainViewModel: MainViewModel,
    productLazyState: LazyGridState,
    permintaanLazyState: LazyListState,
    category: State<Resource<CategoryModel>?>,
    category_id: String
) {
    val topMenuWidth = LocalConfiguration.current.screenWidthDp / MarketTopMenuItem.values().size
    val gridItemWidth = LocalConfiguration.current.screenWidthDp / 2
    val queryNextProduct = remember {
        derivedStateOf {
            viewModel.productList.isNotEmpty()
                    && viewModel.productList.size % 8 == 0
                    && productLazyState
                .layoutInfo
                .visibleItemsInfo
                .lastOrNull()?.index == viewModel.productList.size - 1
                    && category_id.isEmpty()
        }
    }
    val queryNextProductByCategory = remember {
        derivedStateOf {
            viewModel.productList.isNotEmpty()
                    && viewModel.productList.size % 8 == 0
                    && productLazyState
                .layoutInfo
                .visibleItemsInfo
                .lastOrNull()?.index == viewModel.productList.size - 1
                    && category_id.isNotEmpty()
        }
    }
    val queryNextPermintaan = remember {
        derivedStateOf {
            viewModel.permintaanList.isNotEmpty()
                    && viewModel.permintaanList.size % 8 == 0
                    && permintaanLazyState
                .layoutInfo
                .visibleItemsInfo
                .lastOrNull()?.index == viewModel.permintaanList.size - 1
                    && category_id.isEmpty()
        }
    }
    val queryNextPermintaanByCategory = remember {
        derivedStateOf {
            viewModel.permintaanList.isNotEmpty()
                    && viewModel.permintaanList.size % 8 == 0
                    && permintaanLazyState
                .layoutInfo
                .visibleItemsInfo
                .lastOrNull()?.index == viewModel.permintaanList.size - 1
                    && category_id.isNotEmpty()
        }
    }

    LaunchedEffect(key1 = queryNextProduct.value) {
        if (!viewModel.productPageFinished.value) {
            if (queryNextProduct.value) {
                viewModel.loadNextProducts()
            }
        }
    }
    LaunchedEffect(key1 = queryNextPermintaan.value) {
        if (!viewModel.permintaaanPageFinished.value) {
            if (queryNextPermintaan.value) {
                viewModel.loadNextPermintaan()
            }
        }
    }
    LaunchedEffect(key1 = queryNextProductByCategory.value) {
        if (queryNextProductByCategory.value) {
            viewModel.loadNextProductsByCategory(category_id)
        }
    }
    LaunchedEffect(key1 = queryNextPermintaanByCategory.value) {
        if (queryNextPermintaanByCategory.value) {
            viewModel.loadNextPermintaanByCategory(category_id)
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

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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

        // Category
        if (category.value is Resource.Success) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppText(text = "Filtered by", textType = TextType.Body3Semibold)
                category.value?.data?.let {
                    CategoryTag(categoryModel = it)
                }
            }
        }

        // Content
        when (viewModel.selectedTopMenu.value) {
            MarketTopMenuItem.Produk -> {
                // Items
                LazyVerticalGrid(
                    modifier = Modifier.padding(bottom = 16.dp),
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
//                        if (viewModel.productList.isEmpty()) {
                        items(6) {
                            Box(
                                modifier = Modifier
                                    .width(gridItemWidth.dp)
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ProductItemLoading()
                            }
                        }
//                        }
                    }
                }
            }
            MarketTopMenuItem.Permintaan -> {
                LazyColumn(
                    state = permintaanLazyState,
                    modifier = Modifier.padding(bottom = 16.dp),
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
                                            mainViewModel.pickedPermintaanToPermintaanDetailScreen.value =
                                                item
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
//                                if (viewModel.permintaanList.isEmpty()) {
                                items(6) {
                                    PermintaanItemLoading()
                                }
//                                }
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