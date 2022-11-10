package com.bcc.exporeal.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcc.exporeal.model.*
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val searchState = mutableStateOf("")
    val listOfUserInfo = mutableStateListOf<Resource<UserModel>?>()
    val listOfCategory = mutableStateListOf<Resource<CategoryModel>?>()

    private val _banner = MutableStateFlow<Resource<List<BannerModel>>?>(Resource.Loading())
    val banner get() = _banner

    private val _category = MutableStateFlow<Resource<List<CategoryModel>>?>(Resource.Loading())
    val category get() = _category

    private val _top10Product = MutableStateFlow<Resource<List<ProductModel>>?>(Resource.Loading())
    val top10Product get() = _top10Product

    private val _top2Permintaan =
        MutableStateFlow<Resource<List<PermintaanModel>>?>(Resource.Loading())
    val top2Permintaan get() = _top2Permintaan

    fun getCategoryById(
        category_id: String,
        onSuccess: (CategoryModel) -> Unit,
        onFailed: () -> Unit
    ) = viewModelScope.launch {
        repository.getCategoryByCategoryId(category_id).collect {
            when(it){
                is Resource.Error -> {
                    onFailed()
                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    onSuccess(it.data!!)
                }
                null -> {}
            }
        }
    }

    fun getUserById(
        uid: String,
        onSuccess: (UserModel) -> Unit,
        onFailed: () -> Unit
    ) = viewModelScope.launch {
        repository.getUserInfoByUid(uid).collect {
            when(it){
                is Resource.Error -> {
                    onFailed()
                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    onSuccess(it.data!!)
                }
                null -> {}
            }
        }
    }

    init {
        viewModelScope.launch {
            repository.getHomeBanner().collect {
                _banner.value = it
            }
        }
        viewModelScope.launch {
            repository.getTop10Product().collect {
                _top10Product.value = it
            }
        }
        viewModelScope.launch {
            repository.getTop2Permintaan().collect {
                _top2Permintaan.value = it
            }
        }
        viewModelScope.launch {
            repository.getCategories().collect{
                _category.value = it
            }
        }
    }
}