package com.bcc.exporeal.component

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bcc.exporeal.R
import com.bcc.exporeal.model.CategoryModel
import com.bcc.exporeal.model.PermintaanModel
import com.bcc.exporeal.model.ProductModel
import com.bcc.exporeal.model.UserModel
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.Resource
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@Composable
fun ProductItem(
    productModel: ProductModel, onClick: (product: ProductModel) -> Unit
) {
    val itemWidth = LocalConfiguration.current.screenWidthDp / 2.5
    val density = LocalDensity.current
    val clickableBoxWidth = remember { mutableStateOf(0.dp) }
    val clickableBoxHeight = remember { mutableStateOf(0.dp) }

    Box {
        // Content
        Card(
            modifier = Modifier
                .width(width = itemWidth.dp)
                .heightIn(min = (itemWidth * 1.5).dp)
                .onSizeChanged {
                    with(density) {
                        clickableBoxHeight.value = it.height.toDp()
                        clickableBoxWidth.value = it.width.toDp()
                    }
                },
            backgroundColor = AppColor.Neutral10,
            shape = RoundedCornerShape(8.dp),
            elevation = 5.dp
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AsyncImage(
                        modifier = Modifier.size(itemWidth.dp),
                        model = productModel.product_thumbnail ?: "",
                        contentDescription = "Product Image",
                        contentScale = ContentScale.Crop
                    )

                    AppText(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = productModel.product_name ?: "",
                        textType = TextType.Body2,
                        color = AppColor.Neutral100
                    )
                }

                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        text = "Rp${productModel.product_price}",
                        textType = TextType.Body1Semibold,
                        color = AppColor.Warning60
                    )
                    AppText(
                        text = "/${productModel.product_unit}",
                        textType = TextType.Body3,
                        color = AppColor.Neutral60
                    )
                }
            }
        }

        // Clickable box
        Box(modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .size(
                width = clickableBoxWidth.value, height = clickableBoxHeight.value
            )
            .clickable { onClick(productModel) })
    }
}

@Composable
fun ProductItemLoading() {
    val itemWidth = LocalConfiguration.current.screenWidthDp / 2.5

    Card(
        modifier = Modifier
            .width(width = itemWidth.dp)
            .heightIn(min = (itemWidth * 1.5).dp),
        backgroundColor = AppColor.Neutral10,
        shape = RoundedCornerShape(8.dp),
        elevation = 5.dp
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(itemWidth.dp)
                        .placeholder(
                            visible = true,
                            color = AppColor.Neutral50,
                            highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20)
                        )
                )

                AppText(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .placeholder(
                            visible = true,
                            color = AppColor.Neutral50,
                            highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20),
                            shape = RoundedCornerShape(4.dp)
                        ),
                    text = "The Product Name",
                    textType = TextType.Body3,
                    color = AppColor.Neutral100
                )
            }

            AppText(
                modifier = Modifier
                    .padding(8.dp)
                    .placeholder(
                        visible = true,
                        color = AppColor.Neutral50,
                        highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20),
                        shape = RoundedCornerShape(4.dp)
                    ),
                text = "Rp25000",
                textType = TextType.Body3,
                color = AppColor.Warning60
            )
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .clickable { /*TODO*/ })
    }
}

@Composable
fun PermintaanItem(
    onDetailClicked: () -> Unit,
    permintaanModel: PermintaanModel,
    userInfo: Resource<UserModel>?,
    category: Resource<CategoryModel>?
) {
    /**Attrs*/
    val density = LocalDensity.current
    val midDividerHeight = remember { mutableStateOf(0.dp) }

    /**Function*/

    /**Content*/
    Card(
        modifier = Modifier.fillMaxWidth(), elevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Img
                AsyncImage(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop,
                    model = permintaanModel.thumbnail ?: "",
                    contentDescription = "Thumbnail"
                )

                // Top Informations
                Column(
                    modifier = Modifier
                        .height(72.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Kategori & Flag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        //KATEGORI
                        when (category) {
                            is Resource.Error -> {

                            }
                            is Resource.Loading -> {
                                AppText(
                                    text = "CATEGORY",
                                    textType = TextType.Body3,
                                    modifier = Modifier.placeholder(
                                        visible = true,
                                        color = AppColor.Neutral50,
                                        highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20)
                                    )
                                )
                            }
                            is Resource.Success -> {
                                CategoryTag(categoryModel = category.data!!)
                            }
                            null -> {}
                        }

                        //FLAG
                        when (userInfo) {
                            is Resource.Error -> {
                                Box(
                                    modifier = Modifier
                                        .size(width = 51.dp, height = 34.dp)
                                        .background(AppColor.Neutral100)
                                )
                            }
                            is Resource.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .size(width = 51.dp, height = 34.dp)
                                        .placeholder(
                                            visible = true,
                                            color = AppColor.Neutral50,
                                            highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                )
                            }
                            is Resource.Success -> {
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
                                    model = "https://flagcdn.com/h40/${userInfo.data!!.country_id ?: ""}.png",
                                    contentDescription = "Flag"
                                )
                            }
                            null -> {}
                        }
                    }

                    // Product Name
                    AppText(
                        text = permintaanModel.name ?: "",
                        textType = TextType.Body1,
                        color = AppColor.Neutral100
                    )

                    // pengorder name
                    when (userInfo) {
                        is Resource.Error -> {

                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            AppText(
                                text = userInfo.data?.name!!,
                                textType = TextType.Body3,
                                color = AppColor.Neutral60
                            )
                        }
                        null -> { /*TODO*/
                        }
                    }
                }
            }

            // Harga & Kebutuhan
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Harga
                Column(
                    modifier = Modifier.onSizeChanged {
                        with(density) {
                            midDividerHeight.value = it.height.toDp()
                        }
                    }, verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppText(
                        text = "Harga", textType = TextType.Body3, color = AppColor.Neutral60
                    )

                    Row(verticalAlignment = Alignment.Bottom) {
                        AppText(
                            text = "Rp${permintaanModel.bottom_price} - Rp${permintaanModel.top_price}",
                            textType = TextType.H4,
                            color = AppColor.Warning60
                        )
                        AppText(
                            text = "/${permintaanModel.quantity_unit}",
                            textType = TextType.Body2,
                            color = AppColor.Neutral60
                        )
                    }
                }

                // Kebutuhan
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(midDividerHeight.value)
                            .background(AppColor.Neutral60)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppText(
                            text = "Kebutuhan",
                            textType = TextType.Body3,
                            color = AppColor.Neutral60
                        )

                        Row(verticalAlignment = Alignment.Bottom) {
                            AppText(
                                text = "${permintaanModel.quantity}",
                                textType = TextType.H4,
                                color = AppColor.Warning60
                            )
                            AppText(
                                text = "${permintaanModel.quantity_unit}",
                                textType = TextType.Body2,
                                color = AppColor.Neutral60
                            )
                        }
                    }
                }
            }

            // Detail button
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onDetailClicked,
                text = "LIHAT DETAIL",
                textColor = AppColor.Blue60,
                backgroundColor = AppColor.Neutral10,
                borderColor = AppColor.Blue60,
                borderWidth = 1.dp,
                rippleColor = AppColor.Neutral100
            )

            // Tanggal pemesanan
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                AppText(
                    text = "Tanggal permintaan ${permintaanModel.tanggal_permintaan}",
                    textType = TextType.Body3,
                    color = AppColor.Neutral60
                )
            }
        }
    }
}

@Composable
fun PermintaanItemLoading() {
    /**Attrs*/
    val density = LocalDensity.current
    val midDividerHeight = remember { mutableStateOf(0.dp) }

    /**FUnction*/

    /**Content*/
    Card(
        modifier = Modifier.fillMaxWidth(), elevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Img
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .placeholder(
                            visible = true,
                            color = AppColor.Neutral50,
                            highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20)
                        )
                )

                // Top Informations
                Column(
                    modifier = Modifier
                        .height(72.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Kategori & Flag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppText(
                            text = "CATEGORY",
                            textType = TextType.Body3,
                            modifier = Modifier.placeholder(
                                visible = true,
                                color = AppColor.Neutral50,
                                highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20),
                                shape = RoundedCornerShape(4.dp)
                            )
                        )

                        //FLAG
                        Box(
                            modifier = Modifier
                                .height(34.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .border(
                                    width = 1.dp,
                                    shape = RoundedCornerShape(4.dp),
                                    color = AppColor.Neutral60
                                )
                                .placeholder(
                                    visible = true,
                                    color = AppColor.Neutral50,
                                    highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20)
                                ),
                        )
                    }

                    // Product Name
                    AppText(
                        modifier = Modifier.placeholder(
                            visible = true,
                            color = AppColor.Neutral50,
                            highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20),
                            shape = RoundedCornerShape(4.dp)
                        ),
                        text = "Product Name",
                        textType = TextType.Body3,
                        color = AppColor.Neutral100
                    )

                    // pengorder name
                    AppText(
                        modifier = Modifier.placeholder(
                            visible = true,
                            color = AppColor.Neutral50,
                            highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20),
                            shape = RoundedCornerShape(4.dp)
                        ),
                        text = "Username",
                        textType = TextType.Body3,
                        color = AppColor.Neutral60
                    )
                }
            }

            // Harga & Kebutuhan
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Harga
                Column(
                    modifier = Modifier.onSizeChanged {
                        with(density) {
                            midDividerHeight.value = it.height.toDp()
                        }
                    }, verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppText(
                        text = "Harga", textType = TextType.Body3, color = AppColor.Neutral60
                    )

                    AppText(
                        modifier = Modifier.placeholder(
                            visible = true,
                            color = AppColor.Neutral50,
                            highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20),
                            shape = RoundedCornerShape(4.dp)
                        ),
                        text = "RpHarga1 - RpHarga2",
                        textType = TextType.Body3,
                        color = AppColor.Warning60
                    )
                }

                // Kebutuhan
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(midDividerHeight.value)
                            .background(AppColor.Neutral60)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppText(
                            text = "Kebutuhan",
                            textType = TextType.Body3,
                            color = AppColor.Neutral60
                        )

                        AppText(
                            modifier = Modifier.placeholder(
                                visible = true,
                                color = AppColor.Neutral50,
                                highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20)
                            ),
                            text = "Quantity",
                            textType = TextType.Body2,
                            color = AppColor.Neutral60
                        )
                    }
                }
            }

            // Detail button
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { },
                text = "LIHAT DETAIL",
                textColor = AppColor.Blue60,
                backgroundColor = AppColor.Neutral10,
                borderColor = AppColor.Blue60,
                borderWidth = 1.dp,
                rippleColor = AppColor.Neutral100
            )

            // Tanggal pemesanan
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                AppText(
                    modifier = Modifier.placeholder(
                        visible = true,
                        color = AppColor.Neutral50,
                        highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20),
                        shape = RoundedCornerShape(4.dp)
                    ),
                    text = "Tanggal permintaan",
                    textType = TextType.Body3,
                    color = AppColor.Neutral60
                )
            }
        }
    }
}

@Composable
fun AddProductImagePicker(
    onClick: () -> Unit,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColor.Blue20)
            .border(shape = RoundedCornerShape(8.dp), color = AppColor.Blue60, width = 1.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = AppColor.Blue60
            )

            AppText(text = "Add Photo", textType = TextType.Body3, color = AppColor.Blue60)
        }
    }
}

@Composable
fun AddProductImagePreview(
    imgUri: Uri,
    onDeleteClicked: () -> Unit,
    size: Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(AppColor.Neutral100),
        contentAlignment = Alignment.BottomEnd
    ) {
        AsyncImage(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp)),
            model = imgUri,
            contentDescription = "Uri"
        )

        Icon(
            modifier = Modifier
                .clickable {
                    onDeleteClicked()
                },
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = AppColor.Negative60
        )
    }
}