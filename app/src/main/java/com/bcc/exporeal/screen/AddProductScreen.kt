package com.bcc.exporeal.screen

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bcc.exporeal.SnackbarListener
import com.bcc.exporeal.component.*
import com.bcc.exporeal.model.CategoryModel
import com.bcc.exporeal.util.Resource
import com.bcc.exporeal.viewmodel.AddProductViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddProductScreen(navController: NavController) {
    /**Attrs*/
    val viewModel = hiltViewModel<AddProductViewModel>()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { list ->
            if (list.isNotEmpty()) {
                list.forEach { uri ->
                    viewModel.pickedImages.remove(uri)
                }

                viewModel.pickedImages.addAll(list)
            }
        }
    )
    val categories = viewModel.categories.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isLoading.value)

    /**Function*/
    SnackbarListener(
        viewModel.snackbarMessage.value,
        viewModel.showSnackbar
    )

    /**Content*/
    SwipeRefresh(state = swipeRefreshState, onRefresh = { /*TODO*/ }) {
        Scaffold(
            topBar = {
                AppTopBar(onBackClicked = { navController.popBackStack() }, title = "Add Product")
            }
        ) {
            AddProductContent(
                navController = navController,
                viewModel = viewModel,
                launcher = launcher,
                categories = categories
            )
        }
    }
}

@Composable
private fun AddProductContent(
    navController: NavController,
    viewModel: AddProductViewModel,
    launcher: ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>>,
    categories: State<Resource<List<CategoryModel>>?>
) {
    val imgRowState = rememberScrollState()

    LazyColumn(
        modifier = Modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Images
        item {
            Row(
                modifier = Modifier
                    .horizontalScroll(imgRowState)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Picked images here
                viewModel.pickedImages.forEachIndexed { index, uri ->
                    AddProductImagePreview(
                        imgUri = uri, onDeleteClicked = {
                            viewModel.pickedImages.removeAt(index = index)
                        }, size = 128.dp
                    )
                }

                // Add btn
                AddProductImagePicker(size = 128.dp, onClick = {
                    launcher.launch("image/*")
                })
            }
        }

        // Product name
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeHolderText = "Product Name",
                valueState = viewModel.productNameState
            )
        }

        // Category
        item {
            AppDropdownField(
                dropdownModifier = Modifier.fillMaxWidth(),
                contentModifier = Modifier.fillMaxWidth(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    viewModel.isCategoryExpanded.value = !viewModel.isCategoryExpanded.value
                },
                isExpanded = viewModel.isCategoryExpanded,
                onDismissRequest = { viewModel.isCategoryExpanded.value = false },
                placeHolderText = "Category",
                valueState = viewModel.categoryValueState,
            ) {
                if (categories.value is Resource.Success) {
                    categories.value?.data?.let { list ->
                        list.forEachIndexed { index, categoryModel ->
                            AppText(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .clickable {
                                        viewModel.categoryState.value = categoryModel
                                        viewModel.categoryValueState.value =
                                            categoryModel.category_name ?: ""

                                        viewModel.isCategoryExpanded.value = false
                                    },
                                text = categoryModel.category_name ?: "",
                                textType = TextType.Body2
                            )
                        }
                    }
                }
            }
        }

        // Description
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .heightIn(min = 120.dp),
                placeHolderText = "Description",
                valueState = viewModel.descriptionState
            )
        }

        // Price
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextInputField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    placeHolderText = "Price",
                    valueState = viewModel.priceState,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingContent = {
                        AppText(text = "Rp", textType = TextType.Body2Semibold)
                    }
                )

                AppDropdownField(
                    modifier = Modifier,
                    contentModifier = Modifier,
                    onClick = { viewModel.isUnitExpanded.value = !viewModel.isUnitExpanded.value },
                    isExpanded = viewModel.isUnitExpanded,
                    onDismissRequest = { viewModel.isUnitExpanded.value = false },
                    placeHolderText = "Unit",
                    valueState = viewModel.unitState
                ) {
                    listOf("kg", "pcs").forEachIndexed { index, item ->
                        AppText(
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable {
                                    viewModel.unitState.value = item
                                    viewModel.isUnitExpanded.value = false
                                },
                            text = item,
                            textType = TextType.Body2
                        )
                    }
                }
            }
        }

        // Minimum order
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeHolderText = "Minimum Order Quantity (MOQ)",
                valueState = viewModel.minimumOrderState,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        // Stock
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeHolderText = "Stock",
                valueState = viewModel.stockState,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        // publish btn
        item {
            AppButton(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                onClick = {
                    if (viewModel.isAllFieldFilled()) {
                        viewModel.uploadProduct()
                    } else {
                        viewModel.snackbarMessage.value = "Fill all fields before uploading"
                        viewModel.showSnackbar.value = true
                    }
                },
                text = "PUBLISH"
            )
        }
    }
}