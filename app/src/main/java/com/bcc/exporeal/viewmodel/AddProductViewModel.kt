package com.bcc.exporeal.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcc.exporeal.model.CategoryModel
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val pickedImages = mutableStateListOf<Uri>()
    val productNameState = mutableStateOf("")
    val categoryState = mutableStateOf<CategoryModel?>(null)
    val categoryValueState = mutableStateOf("")
    val isCategoryExpanded = mutableStateOf(false)
    val descriptionState = mutableStateOf("")
    val priceState = mutableStateOf("")
    val unitState = mutableStateOf("")
    val isUnitExpanded = mutableStateOf(false)
    val minimumOrderState = mutableStateOf("")
    val stockState = mutableStateOf("")
    val successUploadMediaCount = mutableStateOf(0)
    val showSnackbar = mutableStateOf(false)
    val snackbarMessage = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val thumbnailUrl = mutableStateOf("")

    fun uploadProduct() {
        isLoading.value = true

        repository.getLastCountProduct(
            onSuccess = { lastCount ->
                val newProductId = "${lastCount + 1}-${repository.getRandomKey("product")}"

                repository.saveProductImagesToStorageAndFirestore(
                    product_id = newProductId,
                    listUri = pickedImages,
                    successCountState = successUploadMediaCount,
                    thumbnailUrlState = thumbnailUrl,
                    onSuccess = {
                        repository.saveProductDetail(
                            onSuccess = {
                                snackbarMessage.value = "Product has been created"
                                showSnackbar.value = true
                                isLoading.value = false
                            },
                            onFailed = {
                                snackbarMessage.value = "Error while uploading product data. Try again later"
                                showSnackbar.value = true
                                isLoading.value = false
                            },
                            product_id = newProductId,
                            product_name = productNameState.value,
                            product_quantity = Integer.parseInt(stockState.value),
                            product_unit = unitState.value,
                            product_thumbnail = thumbnailUrl.value,
                            product_price = priceState.value,
                            product_count = lastCount + 1,
                            product_description = descriptionState.value,
                            category_id = categoryState.value?.category_id ?: "0"
                        )
                    },
                    onFailed = {
                        snackbarMessage.value = "Error while uploading images. Try again later"
                        showSnackbar.value = true
                        isLoading.value = false
                    }
                )
            },
            onFailed = {
                snackbarMessage.value = "Can't get product count. Try again later"
                showSnackbar.value = true
                isLoading.value = false
            }
        )
    }

    fun isAllFieldFilled():Boolean{
        when{
            pickedImages.isEmpty() -> return false
            productNameState.value.isEmpty() -> return false
            categoryValueState.value.isEmpty() -> return false
            categoryState.value == null -> return false
            descriptionState.value.isEmpty() -> return false
            priceState.value.isEmpty() -> return false
            unitState.value.isEmpty() -> return false
            minimumOrderState.value.isEmpty() -> return false
            stockState.value.isEmpty() -> return false
        }

        return true
    }

    private val _categories = MutableStateFlow<Resource<List<CategoryModel>>?>(Resource.Loading())
    val categories get() = _categories

    init {
        viewModelScope.launch {
            repository.getCategories().collect {
                _categories.value = it
            }
        }
    }
}