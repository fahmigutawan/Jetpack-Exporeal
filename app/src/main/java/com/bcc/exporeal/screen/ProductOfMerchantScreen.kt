package com.bcc.exporeal.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.bcc.exporeal.component.AppButton
import com.bcc.exporeal.component.AppTextInputField
import com.bcc.exporeal.component.ProductItem
import com.bcc.exporeal.component.ProductItemLoading
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.PagingState
import com.bcc.exporeal.viewmodel.MainViewModel
import com.bcc.exporeal.viewmodel.ProductOfMerchantViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProductOfMerchantScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    /**Attrs*/
    val viewModel = hiltViewModel<ProductOfMerchantViewModel>()
    val gridState = rememberLazyGridState()
    val queryNextProduct = remember {
        derivedStateOf {
            viewModel.productList.isNotEmpty()
                    && viewModel.productList.size % 8 == 0
                    && gridState
                .layoutInfo
                .visibleItemsInfo
                .lastOrNull()?.index == viewModel.productList.size - 1
        }
    }

    /**Function*/
    LaunchedEffect(key1 = queryNextProduct.value) {
        if (!viewModel.pageFinished.value) {
            if (queryNextProduct.value) {
                viewModel.loadNextProducts()
            }
        }
    }

    /**Content*/
    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = AppColor.Neutral10) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier.clickable { navController.popBackStack() },
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )

                    AppTextInputField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        placeHolderText = "Search your product",
                        valueState = viewModel.searchState
                    )
                }
            }
        }
    ) {
        ProductOfMerchantContent(
            navController = navController,
            viewModel = viewModel,
            gridState = gridState,
            mainViewModel = mainViewModel
        )
    }
}

@Composable
private fun ProductOfMerchantContent(
    navController: NavController,
    viewModel: ProductOfMerchantViewModel,
    mainViewModel: MainViewModel,
    gridState: LazyGridState
) {
    val gridItemWidth = LocalConfiguration.current.screenWidthDp / 2

    Column(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        /*Add Prodcuct btn*/
        AppButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onClick = { navController.navigate(route = AppNavRoute.AddProductScreen.name) },
            text = "ADD PRODUCT"
        )

        /*Items*/
        LazyVerticalGrid(
            state = gridState,
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

            if (viewModel.pagingState.value == PagingState.NextLoad) {
//                if (viewModel.productList.isEmpty()) {
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
//                }
            }
        }
    }
}