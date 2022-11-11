package com.bcc.exporeal.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bcc.exporeal.component.*
import com.bcc.exporeal.model.*
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.Resource
import com.bcc.exporeal.viewmodel.HomeViewModel
import com.bcc.exporeal.viewmodel.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    /**Attrs*/
    val viewModel = hiltViewModel<HomeViewModel>()
    val banner = viewModel.banner.collectAsState()
    val category = viewModel.category.collectAsState()
    val top10Product = viewModel.top10Product.collectAsState()
    val top2Permintaan = viewModel.top2Permintaan.collectAsState()

    /**Function*/
    if (top2Permintaan.value is Resource.Success) {
        LaunchedEffect(key1 = true) {
            if (viewModel.listOfUserInfo.size != top2Permintaan.value?.data?.size) {
                for (i in 0..top2Permintaan.value!!.data!!.size - 1) {
                    viewModel.listOfUserInfo.add(Resource.Loading())
                }
            }
        }
        LaunchedEffect(key1 = true) {
            if (viewModel.listOfCategory.size != top2Permintaan.value?.data?.size) {
                for (i in 0..top2Permintaan.value!!.data!!.size - 1) {
                    viewModel.listOfCategory.add(Resource.Loading())
                }
            }
        }
        LaunchedEffect(key1 = true) {
            top2Permintaan.value!!.data?.forEachIndexed { index, item ->
                viewModel.getCategoryById(
                    category_id = item.category_id ?: "",
                    onSuccess = {
                        viewModel.listOfCategory[index] = Resource.Success(it)
                    },
                    onFailed = {
                        /*TODO*/
                    }
                )

                viewModel.getUserById(
                    uid = item.peminta_uid ?: "",
                    onSuccess = {
                        viewModel.listOfUserInfo[index] = Resource.Success(it)
                    },
                    onFailed = {
                        /*TODO*/
                    }
                )
            }
        }
    }

    /**Content*/
    HomeContent(
        navController = navController,
        viewModel = viewModel,
        mainViewModel = mainViewModel,
        banner = banner,
        category = category,
        top10Product = top10Product,
        top2Permintaan = top2Permintaan
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun HomeContent(
    navController: NavController,
    viewModel: HomeViewModel,
    mainViewModel: MainViewModel,
    banner: State<Resource<List<BannerModel>>?>,
    category: State<Resource<List<CategoryModel>>?>,
    top10Product: State<Resource<List<ProductModel>>?>,
    top2Permintaan: State<Resource<List<PermintaanModel>>?>
) {
    val density = LocalDensity.current
    val scrWidth = remember { mutableStateOf(0.dp) }
    val scrHeight = remember { mutableStateOf(0.dp) }
    val viewPagerState = rememberPagerState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .onSizeChanged {
                with(density) {
                    scrWidth.value = it.width.toDp()
                    scrHeight.value = (it.width.toDp()) * 9 / 16
                }
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Pager
        item {
            when (banner.value) {
                is Resource.Error -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(
                                    width = scrWidth.value,
                                    height = scrHeight.value
                                )
                                .background(AppColor.Neutral50)
                        )

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            HorizontalPagerIndicator(
                                pagerState = viewPagerState,
                                activeColor = AppColor.Blue60,
                                inactiveColor = AppColor.Neutral50
                            )
                        }
                    }
                }
                is Resource.Loading -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(
                                    width = scrWidth.value,
                                    height = scrHeight.value
                                )
                                .placeholder(
                                    visible = true,
                                    color = AppColor.Neutral50,
                                    highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral10)
                                )
                        )

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            HorizontalPagerIndicator(
                                pagerState = viewPagerState,
                                activeColor = AppColor.Blue60,
                                inactiveColor = AppColor.Neutral50
                            )
                        }
                    }
                }
                is Resource.Success -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HorizontalPager(
                            count = banner.value?.data?.size ?: 0,
                            state = viewPagerState
                        ) { index ->
                            Log.e("URL", banner.value?.data?.get(index)?.url.toString())
                            Box(
                                modifier = Modifier
                                    .size(
                                        width = scrWidth.value,
                                        height = scrHeight.value
                                    ), contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = banner.value?.data?.get(index)?.url ?: "",
                                    contentDescription = "Image"
                                )
                            }
                        }

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            HorizontalPagerIndicator(
                                pagerState = viewPagerState,
                                activeColor = AppColor.Blue60,
                                inactiveColor = AppColor.Neutral50
                            )
                        }
                    }
                }
                null -> {}
            }
        }

        // Kategori title
        item {
            AppText(text = "Kategori", textType = TextType.H3, color = AppColor.Neutral100)
        }

        // Kategory items
        item {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier.horizontalScroll(state = scrollState),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (category.value) {
                    is Resource.Error -> {

                    }
                    is Resource.Loading -> {
                        repeat(8) {
                            Column(
                                modifier = Modifier,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
//                                AsyncImage(
//                                    modifier = Modifier.size(48.dp),
//                                    model = item.iconId,
//                                    contentDescription = "Icon"
//                                )

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .placeholder(
                                            visible = true,
                                            color = AppColor.Neutral50,
                                            highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20)
                                        )
                                )

                                AppText(
                                    modifier = Modifier
                                        .placeholder(
                                            visible = true,
                                            color = AppColor.Neutral50,
                                            highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20),
                                            shape = RoundedCornerShape(4.dp)
                                        ),
                                    text = "Catgegory",
                                    textType = TextType.BottomMenu,
                                    color = AppColor.Blue60
                                )

                            }
                        }
                    }
                    is Resource.Success -> {
                        category.value?.data?.let {
                            it.forEach { categoryModel ->
                                Column(
                                    modifier = Modifier,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AsyncImage(
                                        modifier = Modifier.size(48.dp),
                                        model = categoryModel.image,
                                        contentDescription = "Icon"
                                    )

                                    AppText(
                                        text = categoryModel.category_name ?: "",
                                        textType = TextType.BottomMenu,
                                        color = AppColor.Blue60
                                    )
                                }
                            }
                        }
                    }
                    null -> {}
                }
            }
        }

        // Produk pilihan title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppText(
                    text = "Produk pilihan",
                    textType = TextType.H3,
                    color = AppColor.Neutral100
                )

                AppTextButton(
                    text = "Lihat semua",
                    textType = TextType.Body3Semibold,
                    onClick = { /*TODO*/ },
                    color = AppColor.Blue60
                )
            }
        }

        // Produk pilihan items
        item {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier.horizontalScroll(state = scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.width(0.dp))
                when (top10Product.value) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {
                        repeat(4) {
                            ProductItemLoading()
                        }
                    }
                    is Resource.Success -> {
                        top10Product.value?.data?.let {
                            it.forEach {
                                ProductItem(
                                    productModel = it,
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
                    null -> {}
                }
                Spacer(modifier = Modifier.width(0.dp))
            }
        }

        // Permintaan terbaru title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppText(
                    text = "Permintaan terbaru",
                    textType = TextType.H3,
                    color = AppColor.Neutral100
                )

                AppTextButton(
                    text = "Lihat semua",
                    textType = TextType.Body3Semibold,
                    onClick = { /*TODO*/ },
                    color = AppColor.Blue60
                )
            }
        }

        // Permintaan terbaru items
        when (top2Permintaan.value) {
            is Resource.Error -> {

            }
            is Resource.Loading -> {
                items(2) {
                    PermintaanItemLoading()
                }
            }
            is Resource.Success -> {
                itemsIndexed(top2Permintaan.value?.data!!) { index, item ->
                    if (viewModel.listOfUserInfo.size == top2Permintaan.value?.data?.size
                        && viewModel.listOfCategory.size == top2Permintaan.value?.data?.size
                    ) {
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
            null -> { /*TODO*/
            }
        }

        // spacer
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
