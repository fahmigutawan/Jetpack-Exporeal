package com.bcc.exporeal.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bcc.exporeal.R
import com.bcc.exporeal.component.*
import com.bcc.exporeal.model.CategoryModel
import com.bcc.exporeal.model.ProductPictureModel
import com.bcc.exporeal.model.UserModel
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.Resource
import com.bcc.exporeal.viewmodel.MainViewModel
import com.bcc.exporeal.viewmodel.PermintaanDetailViewModel
import com.bcc.exporeal.viewmodel.ProductDetailViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PermintaanDetailScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    /**Attrs*/
    val viewModel = hiltViewModel<PermintaanDetailViewModel>()
    val productPictures = viewModel.productPictures.collectAsState()
    val category = viewModel.category.collectAsState()
    val merchant = viewModel.merchant.collectAsState()

    /**Function*/
    if (productPictures.value is Resource.Loading) {
        LaunchedEffect(key1 = true) {
            viewModel.getProductPicturesByProductId(
                mainViewModel.pickedPermintaanToPermintaanDetailScreen.value!!.permintaan_id!!
            )
        }
    }
    if (category.value is Resource.Loading) {
        LaunchedEffect(key1 = true) {
            viewModel.getCategoryByCategoryId(
                mainViewModel.pickedPermintaanToPermintaanDetailScreen.value!!.category_id!!
            )
        }
    }
    if (merchant.value is Resource.Loading) {
        LaunchedEffect(key1 = true) {
            viewModel.getMerchantByUid(
                mainViewModel.pickedPermintaanToPermintaanDetailScreen.value!!.peminta_uid!!
            )
        }
    }

    /**Content*/
    Scaffold(
        topBar = {

        },
        bottomBar = {
            BottomAppBar(backgroundColor = AppColor.Neutral10, elevation = 10.dp) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppButton(
                        modifier = Modifier.fillMaxSize(),
                        onClick = { /*TODO*/ },
                        text = "CONTACT BUYER"
                    )
                }
            }
        }
    ) {
        PermintaanDetailContent(
            modifier = Modifier.padding(bottom = it.calculateBottomPadding()),
            navController = navController,
            viewModel = viewModel,
            mainViewModel = mainViewModel,
            productPictures = productPictures,
            category = category,
            merchant = merchant
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalPagerApi
@Composable
private fun PermintaanDetailContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PermintaanDetailViewModel,
    mainViewModel: MainViewModel,
    productPictures: State<Resource<List<ProductPictureModel>>?>,
    category: State<Resource<CategoryModel>?>,
    merchant: State<Resource<UserModel>?>
) {
    val imgPagerState = rememberPagerState()
    val imgWidth = LocalConfiguration.current.screenWidthDp
    val imgHeight = imgWidth * 3 / 4

    mainViewModel.pickedPermintaanToPermintaanDetailScreen.value?.let { productModel ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(AppColor.Neutral20),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pictures
            when (productPictures.value) {
                is Resource.Error -> {
                    val imgWithThumbnail = listOf(
                        productModel.thumbnail
                    )
                    item {
                        Box(
                            modifier = Modifier.size(
                                width = imgWidth.dp,
                                height = imgHeight.dp
                            ), contentAlignment = Alignment.Center
                        ) {
                            HorizontalPager(
                                modifier = Modifier.size(
                                    width = imgWidth.dp,
                                    height = imgHeight.dp
                                ), count = imgWithThumbnail.size
                            ) { index ->
                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = imgWithThumbnail[index] ?: "",
                                    contentDescription = "Img"
                                )
                            }
                        }
                    }
                }
                is Resource.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .size(width = imgWidth.dp, height = imgHeight.dp)
                                .placeholder(
                                    visible = true,
                                    color = AppColor.Neutral50,
                                    highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20)
                                )
                        )
                    }
                }
                is Resource.Success -> {
                    productPictures.value?.data?.let {
                        val imgWithThumbnail = listOf(
                            productModel.thumbnail
                        ) + it.map { it.picture_url }
                        item {
                            // Container img & foreground btn
                            Box(
                                modifier = Modifier
                                    .size(
                                        width = imgWidth.dp,
                                        height = imgHeight.dp
                                    )
                                    .background(color = AppColor.Neutral100),
                                contentAlignment = Alignment.Center
                            ) {
                                // Imgs
                                HorizontalPager(
                                    state = imgPagerState,
                                    modifier = Modifier.size(
                                        width = imgWidth.dp,
                                        height = imgHeight.dp
                                    ), count = imgWithThumbnail.size
                                ) { index ->
                                    AsyncImage(
                                        modifier = Modifier.fillMaxSize(),
                                        model = imgWithThumbnail[index] ?: "",
                                        contentDescription = "Img"
                                    )
                                }

                                // Foreground btn
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    IconButton(
                                        modifier = Modifier
                                            .alpha(0.7f)
                                            .clip(CircleShape)
                                            .background(
                                                color = when {
                                                    imgPagerState.currentPage - 1 > 0 -> AppColor.Neutral60
                                                    else -> AppColor.Neutral20
                                                }
                                            ),
                                        onClick = { /*TODO*/ },
                                        enabled = imgPagerState.currentPage - 1 > 0
                                    ) {
                                        Icon(
                                            tint = when {
                                                imgPagerState.currentPage - 1 > 0 -> AppColor.Neutral10
                                                else -> AppColor.Neutral40
                                            },
                                            imageVector = Icons.Default.ArrowLeft,
                                            contentDescription = "Back"
                                        )
                                    }

                                    IconButton(
                                        modifier = Modifier
                                            .alpha(0.7f)
                                            .clip(CircleShape)
                                            .background(
                                                color = when {
                                                    imgPagerState.currentPage + 1 < imgWithThumbnail.size - 1 -> AppColor.Neutral60
                                                    else -> AppColor.Neutral20
                                                }
                                            ),
                                        onClick = { /*TODO*/ },
                                        enabled = imgPagerState.currentPage + 1 < imgWithThumbnail.size - 1
                                    ) {
                                        Icon(
                                            tint = when {
                                                imgPagerState.currentPage + 1 < imgWithThumbnail.size - 1 -> AppColor.Neutral10
                                                else -> AppColor.Neutral40
                                            },
                                            imageVector = Icons.Default.ArrowRight,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                null -> {}
            }

            // Category
            when (category.value) {
                is Resource.Error -> {

                }
                is Resource.Loading -> {
                    item {
                        AppText(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .placeholder(
                                    visible = true,
                                    color = AppColor.Neutral50,
                                    highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20),
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            text = "Category",
                            textType = TextType.Body3
                        )
                    }
                }
                is Resource.Success -> {
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            CategoryTag(categoryModel = category.value!!.data!!)
                        }
                    }
                }
                null -> {}
            }

            // Harga & product name
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Harga
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppText(
                            text = "Rp${mainViewModel.pickedPermintaanToPermintaanDetailScreen.value!!.bottom_price!!} - Rp${mainViewModel.pickedPermintaanToPermintaanDetailScreen.value!!.top_price!!}",
                            textType = TextType.H3,
                            color = AppColor.Warning60
                        )

                        AppText(
                            text = "/${mainViewModel.pickedPermintaanToPermintaanDetailScreen.value!!.quantity_unit!!}",
                            textType = TextType.Body3,
                            color = AppColor.Neutral60
                        )
                    }

                    // Name
                    AppText(
                        text = mainViewModel.pickedPermintaanToPermintaanDetailScreen.value!!.name!!,
                        textType = TextType.Body1
                    )
                }
            }

            // Deskripsi
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AppText(text = "Deskripsi", textType = TextType.H4)

                    mainViewModel.pickedPermintaanToPermintaanDetailScreen.value?.let { product ->
                        Column {
                            AnimatedContent(
                                targetState = viewModel.expandDescription.value
                            ) { state ->
                                when {
                                    state -> {
                                        AppText(
                                            text = product.description ?: "",
                                            textType = TextType.Body2
                                        )
                                    }

                                    else -> {
                                        when {
                                            (product.description ?: "").length > 150 -> {
                                                AppText(
                                                    text = (product.description
                                                        ?: "").substring(0, 151),
                                                    textType = TextType.Body2
                                                )
                                            }
                                            else -> {
                                                AppText(
                                                    text = product.description ?: "",
                                                    textType = TextType.Body2
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if ((product.description ?: "").length > 150) {
                                AppTextButton(
                                    text = when {
                                        viewModel.expandDescription.value -> "Less.."
                                        else -> "More.."
                                    },
                                    textType = TextType.Body2Semibold,
                                    onClick = {
                                        viewModel.expandDescription.value =
                                            !viewModel.expandDescription.value
                                    })
                            }
                        }
                    }
                }
            }

            // Erder
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AppText(text = "Order Quantity", textType = TextType.H4)

                    /*TODO the text below here*/
                }
            }

            // Seller profile
            when (merchant.value) {
                is Resource.Error -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            AppText(text = "Buyer Profile", textType = TextType.H4)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(AppColor.Neutral100)
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AppText(
                                        text = "...",
                                        textType = TextType.H4
                                    )
                                }
                            }
                        }
                    }
                }
                is Resource.Loading -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            AppText(text = "Buyer Profile", textType = TextType.H4)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .placeholder(
                                            visible = true,
                                            color = AppColor.Neutral50,
                                            highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20)
                                        )
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AppText(
                                        modifier = Modifier.placeholder(
                                            visible = true,
                                            color = AppColor.Neutral50,
                                            highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20),
                                            shape = RoundedCornerShape(4.dp)
                                        ),
                                        text = "My Name",
                                        textType = TextType.Body3
                                    )
                                }
                            }
                        }
                    }
                }
                is Resource.Success -> {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            AppText(text = "Buyer Profile", textType = TextType.H4)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AsyncImage(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    model = merchant.value?.data?.profile_pic ?: "",
                                    contentDescription = "Profile pic"
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AppText(
                                        text = merchant.value?.data?.name ?: "",
                                        textType = TextType.Body1Semibold
                                    )

                                    AsyncImage(
                                        modifier = Modifier
                                            .height(34.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .border(
                                                width = 1.dp,
                                                shape = RoundedCornerShape(4.dp),
                                                color = AppColor.Neutral30
                                            ),
                                        model = "https://countryflagsapi.com/png/${merchant.value?.data?.country_id ?: ""}",
                                        contentDescription = "Flag"
                                    )
                                }
                            }
                        }
                    }
                }
                null -> {}
            }
        }
    }
}