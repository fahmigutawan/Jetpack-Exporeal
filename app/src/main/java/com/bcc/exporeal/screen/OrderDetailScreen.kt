package com.bcc.exporeal.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bcc.exporeal.R
import com.bcc.exporeal.SnackbarListener
import com.bcc.exporeal.component.*
import com.bcc.exporeal.model.AddressModel
import com.bcc.exporeal.model.UserModel
import com.bcc.exporeal.screen.OrderDetailScreenState.*
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.ListenAppBackHandler
import com.bcc.exporeal.util.Resource
import com.bcc.exporeal.viewmodel.MainViewModel
import com.bcc.exporeal.viewmodel.OrderDetailViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun OrderDetailScreen(
    navController: NavController, mainViewModel: MainViewModel
) {
    /**Attrs*/
    val viewModel = hiltViewModel<OrderDetailViewModel>()
    val address = viewModel.address.collectAsState()
    val user = viewModel.userInfo.collectAsState()
    val merchant = viewModel.merchantInfo.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isLoading.value)

    /**Function*/
    ListenAppBackHandler {
        when (viewModel.orderDetailScreenState.value) {
            Main -> navController.popBackStack()
            AddressList -> viewModel.orderDetailScreenState.value = Main
            AddAddress -> viewModel.orderDetailScreenState.value = AddressList
        }
    }
    if (merchant.value is Resource.Loading) {
        LaunchedEffect(key1 = true) {
            viewModel.getMerchantInfo(
                mainViewModel.pickedProductToProductDetailScreen.value!!.seller_id ?: ""
            )
        }
    }
    if (viewModel.isLoading.value) {
        LaunchedEffect(key1 = true) {
            delay(3000)
            viewModel.isLoading.value = false
        }
    }
    SnackbarListener(viewModel.snackbarMessage.value, viewModel.showSnackbar)
    viewModel.subTotalProduct.value = viewModel.pickedQuantity.value * Integer.parseInt(
        mainViewModel.pickedProductToProductDetailScreen.value!!.product_price ?: "0"
    )

    /**Content*/
    Scaffold(
        topBar = {
            AppTopBar(
                onBackClicked = { navController.popBackStack() },
                title = when (viewModel.orderDetailScreenState.value) {
                    Main -> "Order Detail"
                    AddressList -> "Address List"
                    AddAddress -> "Add New Address"
                }
            )
        },
        bottomBar = {
            if(viewModel.orderDetailScreenState.value == Main){
                BottomAppBar(backgroundColor = AppColor.Neutral10, elevation = 10.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            AppText(text = "Total", textType = TextType.Body3)
                            AppText(
                                text = ((viewModel.subTotalDelivery.value + viewModel.subTotalProduct.value).toString()),
                                textType = TextType.Body2Semibold,
                                color = AppColor.Warning60
                            )
                        }

                        AppButton(
                            onClick = {
                                viewModel.apply {
                                    if (pickedAddress.value == null) {
                                        snackbarMessage.value = "Choose delivery address"
                                        showSnackbar.value = true
                                        return@apply
                                    }

                                    /*TODO*/
                                }
                            },
                            text = "ORDER"
                        )
                    }
                }
            }
        }
    ) {
        SwipeRefresh(
            modifier = Modifier.padding(bottom = it.calculateBottomPadding()),
            state = swipeRefreshState,
            onRefresh = { viewModel.refresh() }
        ) {
            when (viewModel.orderDetailScreenState.value) {
                Main -> {
                    OrderDetailContent(
                        navController = navController,
                        viewModel = viewModel,
                        mainViewModel = mainViewModel,
                        address = address,
                        userInfo = user,
                        merchant = merchant
                    )
                }
                AddressList -> {
                    OrderDetailAddresses(viewModel = viewModel, address = address)
                }
                AddAddress -> {
                    OrderDetailAddAddress(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun OrderDetailContent(
    navController: NavController,
    viewModel: OrderDetailViewModel,
    mainViewModel: MainViewModel,
    address: State<Resource<List<AddressModel>>?>,
    userInfo: State<Resource<UserModel>?>,
    merchant: State<Resource<UserModel>?>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral20),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Spacer
        item {
            Spacer(Modifier)
        }

        // Warning
        item {
            AppWarningText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = "we are not responsible for any transactions outside the application"
            )
        }

        // Location
        item {
            AddressItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(4.dp)),
                onChooseAnotherClicked = { viewModel.orderDetailScreenState.value = AddressList },
                address = viewModel.pickedAddress.value
            )
        }

        // Order
        item {
            val imgSize = remember { mutableStateOf(0.dp) }
            val cardWidth = remember { mutableStateOf(0.dp) }
            val density = LocalDensity.current

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .onSizeChanged {
                    with(density) {
                        cardWidth.value = it.width.toDp()
                    }
                }
                .clip(RoundedCornerShape(4.dp))
                .background(AppColor.Neutral10)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Title
                    AppText(text = "Order", textType = TextType.Body2Semibold)

                    // Merchant
                    when (merchant.value) {
                        is Resource.Error -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(AppColor.Neutral100)
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AppText(
                                        text = "...", textType = TextType.H4
                                    )
                                }
                            }
                        }
                        is Resource.Loading -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .placeholder(
                                            visible = true,
                                            color = AppColor.Neutral50,
                                            highlight = PlaceholderHighlight.shimmer(
                                                highlightColor = AppColor.Neutral20
                                            )
                                        )
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AppText(
                                        modifier = Modifier.placeholder(
                                            visible = true,
                                            color = AppColor.Neutral50,
                                            highlight = PlaceholderHighlight.shimmer(
                                                highlightColor = AppColor.Neutral20
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        ), text = "My Name", textType = TextType.Body3
                                    )
                                }
                            }
                        }
                        is Resource.Success -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                when (merchant.value?.data?.profile_pic ?: "") {
                                    "" -> {
                                        AsyncImage(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop,
                                            model = R.drawable.ic_profile_pic,
                                            contentDescription = "Profile pic"
                                        )
                                    }

                                    else -> {
                                        AsyncImage(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop,
                                            model = merchant.value?.data?.profile_pic ?: "",
                                            contentDescription = "Profile pic"
                                        )
                                    }
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AppText(
                                        text = merchant.value?.data?.name ?: "",
                                        textType = TextType.Body1Semibold
                                    )

                                    if (merchant.value?.data?.country_id != null) {
                                        AsyncImage(
                                            modifier = Modifier
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .border(
                                                    width = 1.dp,
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = AppColor.Neutral30
                                                ),
                                            model = "https://flagcdn.com/h40/${merchant.value?.data?.country_id ?: ""}.png",
                                            contentDescription = "Flag"
                                        )
                                    } else {
                                        AsyncImage(
                                            modifier = Modifier
                                                .height(34.dp)
                                                .width(51.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .border(
                                                    width = 1.dp,
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = AppColor.Neutral30
                                                ),
                                            model = R.drawable.ic_baseline_rectangle_24,
                                            contentDescription = "Flag"
                                        )
                                    }
                                }
                            }
                        }
                        null -> {}
                    }

                    // Order item
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AsyncImage(
                            modifier = Modifier
                                .size(imgSize.value)
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop,
                            model = mainViewModel.pickedProductToProductDetailScreen.value!!.product_thumbnail
                                ?: "",
                            contentDescription = "Img"
                        )

                        Column(modifier = Modifier.onSizeChanged {
                            with(density) {
                                imgSize.value = it.height.toDp()
                            }
                        }) {
                            AppText(
                                text = mainViewModel.pickedProductToProductDetailScreen.value!!.product_name
                                    ?: "", textType = TextType.Body2
                            )

                            AppText(
                                text = "${
                                    mainViewModel.pickedProductToProductDetailScreen.value!!.product_price ?: ""
                                }/${
                                    mainViewModel.pickedProductToProductDetailScreen.value!!.product_unit ?: ""
                                }", textType = TextType.Body2Semibold
                            )
                        }
                    }

                    // Quantity
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppText(
                            text = "Quatity", textType = TextType.Body3, color = AppColor.Neutral60
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        if (viewModel.pickedQuantity.value > 1) {
                                            viewModel.pickedQuantity.value -= 1
                                        }
                                    }, model = R.drawable.ic_minus, contentDescription = "minus"
                            )

                            AppText(
                                text = viewModel.pickedQuantity.value.toString(),
                                textType = TextType.Body3
                            )

                            AsyncImage(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        if (viewModel.pickedQuantity.value < mainViewModel.pickedProductToProductDetailScreen.value!!.product_quantity!!) {
                                            viewModel.pickedQuantity.value += 1
                                        }
                                    }, model = R.drawable.ic_plus, contentDescription = "plus"
                            )
                        }
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .width(cardWidth.value)
                            .background(AppColor.Neutral60)
                    )

                    // Subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppText(
                            text = "Subtotal", textType = TextType.Body3, color = AppColor.Neutral60
                        )

                        AppText(
                            text = (viewModel.subTotalProduct.value.toString()),
                            textType = TextType.Body2Semibold,
                            color = AppColor.Warning60
                        )
                    }
                }
            }
        }

        // Shipping and Payment
        item {
            val cardWidth = remember { mutableStateOf(0.dp) }
            val density = LocalDensity.current
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .onSizeChanged {
                    with(density) {
                        cardWidth.value = it.width.toDp()
                    }
                }
                .clip(RoundedCornerShape(4.dp))
                .background(AppColor.Neutral10)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Title
                    AppText(text = "Shipping and Payment", textType = TextType.Body2Semibold)

                    // Delivery section
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /*TODO*/ }) {
                        Column {
                            // Shipper
                            AppText(text = "Maersk Line", textType = TextType.Body2Semibold)

                            // Fee
                            AppText(
                                text = "${viewModel.deliveryPerUnit.value}/kg",
                                textType = TextType.Body2Semibold
                            )

                            // Received by
                            AppText(
                                text = "Received by 21 - 28 Dec",
                                textType = TextType.Body3,
                                color = AppColor.Neutral60
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.NavigateNext,
                            contentDescription = "Icon",
                            tint = AppColor.Neutral60
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .width(cardWidth.value)
                            .background(AppColor.Neutral60)
                    )

                    // Paypal (DUMMY)
                    AsyncImage(
                        modifier = Modifier.height(42.dp),
                        model = R.drawable.ic_paypal,
                        contentDescription = "paypal"
                    )

                    // Divider
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .width(cardWidth.value)
                            .background(AppColor.Neutral60)
                    )

                    // Subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppText(
                            text = "Subtotal", textType = TextType.Body3, color = AppColor.Neutral60
                        )

                        AppText(
                            text = (viewModel.subTotalDelivery.value.toString()),
                            textType = TextType.Body2Semibold,
                            color = AppColor.Warning60
                        )
                    }
                }
            }
        }

        // Summary
        item {
            val cardWidth = remember { mutableStateOf(0.dp) }
            val density = LocalDensity.current
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .onSizeChanged {
                    with(density) {
                        cardWidth.value = it.width.toDp()
                    }
                }
                .clip(RoundedCornerShape(4.dp))
                .background(AppColor.Neutral10)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Title
                    AppText(text = "Summary", textType = TextType.Body2Semibold)

                    // Price of goods
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppText(
                            text = "Price of Goods",
                            textType = TextType.Body3,
                            color = AppColor.Neutral100
                        )

                        AppText(
                            text = (viewModel.subTotalProduct.value.toString()),
                            textType = TextType.Body3
                        )
                    }

                    // Price of goods
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppText(
                            text = "Price of Shipping",
                            textType = TextType.Body3,
                            color = AppColor.Neutral100
                        )

                        AppText(
                            text = (viewModel.subTotalDelivery.value.toString()),
                            textType = TextType.Body3
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .width(cardWidth.value)
                            .background(AppColor.Neutral60)
                    )

                    // Subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppText(
                            text = "Total", textType = TextType.Body3, color = AppColor.Neutral60
                        )

                        AppText(
                            text = ((viewModel.subTotalDelivery.value + viewModel.subTotalProduct.value).toString()),
                            textType = TextType.Body2Semibold,
                            color = AppColor.Warning60
                        )
                    }
                }
            }
        }

        // Bottom Spacer
        item {
            Spacer(Modifier)
        }
    }
}

@Composable
private fun OrderDetailAddresses(
    viewModel: OrderDetailViewModel, address: State<Resource<List<AddressModel>>?>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral20),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier)
        }

        when (address.value) {
            is Resource.Error -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppText(
                            text = "Error while retrieving addresses",
                            textType = TextType.Body2,
                            color = AppColor.Negative60
                        )
                    }
                }
            }
            is Resource.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColor.Blue60)
                    }
                }
            }
            is Resource.Success -> {
                if (address.value?.data?.isEmpty() != false) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AppText(
                                text = "You have no addresses added. Add new address by click the button below",
                                textType = TextType.Body2,
                                align = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(address.value?.data ?: listOf()) { item ->
                        Box(modifier = Modifier.clickable {
                            viewModel.pickedAddress.value = item
                            viewModel.orderDetailScreenState.value = Main
                        }) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Address
                                AppText(text = item.address ?: "", textType = TextType.Body1)

                                // divider
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .height(1.dp)
                                        .background(AppColor.Neutral50)
                                )
                            }
                        }
                    }
                }
            }
            null -> {}
        }

        item {
            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = { viewModel.orderDetailScreenState.value = AddAddress },
                text = "ADD NEW ADDRESS"
            )
        }

        item {
            Spacer(Modifier)
        }
    }
}

@Composable
private fun OrderDetailAddAddress(
    viewModel: OrderDetailViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Neutral20),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier)
        }

        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .heightIn(min = 120.dp),
                placeHolderText = "Your Address",
                valueState = viewModel.addAddressState,
                singleLine = false
            )
        }

        item {
            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), onClick = {
                    viewModel.isLoading.value = true
                    viewModel.saveAddress(address = viewModel.addAddressState.value, onSuccess = {
                        viewModel.refresh()
                        viewModel.snackbarMessage.value = "Address has been added"
                        viewModel.showSnackbar.value = true
                        viewModel.pickedAddress.value = it
                        viewModel.orderDetailScreenState.value = Main
                    }, onFailed = {
                        viewModel.snackbarMessage.value = "Something went wrong, try again later"
                        viewModel.showSnackbar.value = true
                    })
                }, text = "ADD"
            )
        }

        item {
            Spacer(Modifier)
        }
    }
}

enum class OrderDetailScreenState {
    Main, AddressList, AddAddress
}