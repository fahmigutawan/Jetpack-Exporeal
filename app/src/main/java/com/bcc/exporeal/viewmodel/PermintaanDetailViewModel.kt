package com.bcc.exporeal.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcc.exporeal.model.CategoryModel
import com.bcc.exporeal.model.ProductPictureModel
import com.bcc.exporeal.model.UserModel
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermintaanDetailViewModel @Inject constructor(
    private val repository: AppRepository
):ViewModel() {
    val expandDescription = mutableStateOf(false)
    val showSnackbar = mutableStateOf(false)
    val snackbarMessage = mutableStateOf("")

    private val _productPictures =
        MutableStateFlow<Resource<List<ProductPictureModel>>?>(Resource.Loading())
    val productPictures get() = _productPictures

    private val _category = MutableStateFlow<Resource<CategoryModel>?>(Resource.Loading())
    val category get() = _category

    private val _merchant = MutableStateFlow<Resource<UserModel>?>(Resource.Loading())
    val merchant get() = _merchant

    fun getProductPicturesByProductId(product_id:String){
        viewModelScope.launch {
            repository.getProductPicturesByProductId(product_id).collect{
                _productPictures.value = it
            }
        }
    }

    fun getCategoryByCategoryId(category_id:String){
        viewModelScope.launch {
            repository.getCategoryByCategoryId(category_id).collect{
                _category.value = it
            }
        }
    }

    fun getMerchantByUid(uid:String){
        viewModelScope.launch {
            repository.getUserInfoByUid(uid).collect{
                _merchant.value = it
            }
        }
    }

    fun getCurrentUid() = repository.getCurrentUid()
}