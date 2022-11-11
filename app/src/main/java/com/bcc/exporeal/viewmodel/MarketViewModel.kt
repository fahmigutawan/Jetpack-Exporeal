package com.bcc.exporeal.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcc.exporeal.model.CategoryModel
import com.bcc.exporeal.model.PermintaanModel
import com.bcc.exporeal.model.ProductModel
import com.bcc.exporeal.model.UserModel
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.screen.MarketTopMenuItem
import com.bcc.exporeal.util.PagingState
import com.bcc.exporeal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val repository: AppRepository
):ViewModel() {
    val selectedTopMenu = mutableStateOf(MarketTopMenuItem.Produk)
    val productPagingState = mutableStateOf(PagingState.Success)
    val permintaanPagingState = mutableStateOf(PagingState.Success)
    val productList = mutableStateListOf<ProductModel>()
    val permintaanList = mutableStateListOf<PermintaanModel>()
    val listOfUserInfo = mutableStateListOf<Resource<UserModel>?>()
    val listOfCategory = mutableStateListOf<Resource<CategoryModel>?>()
    val productPageFinished = mutableStateOf(false)
    val permintaaanPageFinished = mutableStateOf(false)

    fun loadFirstProducts() = viewModelScope.launch {
        repository.getFirstProductsWithNoFilter().collect{
            when(it){
                is Resource.Error -> {
                    productPagingState.value = PagingState.NextLoadError
                }
                is Resource.Loading -> {
                    productPagingState.value = PagingState.NextLoad
                }
                is Resource.Success -> {
                    productPagingState.value = PagingState.Success
                    it.data?.let { list ->
                        productList.addAll(list)
                    }
                }
                null -> { }
            }
        }
    }

    fun loadNextProducts() = viewModelScope.launch {
        repository.getNextProductsWithNoFilter(
            lastVisiblePostCount = productList.last().product_count ?: 0
        ).collect{
            when(it){
                is Resource.Error -> {
                    productPagingState.value = PagingState.NextLoadError
                }
                is Resource.Loading -> {
                    productPagingState.value = PagingState.NextLoad
                }
                is Resource.Success -> {
                    productPagingState.value = PagingState.Success
                    it.data?.let { list ->
                        if(list.isEmpty()) productPageFinished.value = true
                        productList.addAll(list)
                    }
                }
                null -> { }
            }
        }
    }

    fun loadFirstPermintaan() = viewModelScope.launch {
        repository.getFirstPermintaanWithNoFilter().collect{
            when(it){
                is Resource.Error -> {
                    permintaanPagingState.value = PagingState.NextLoadError
                }
                is Resource.Loading -> {
                    permintaanPagingState.value = PagingState.NextLoad
                }
                is Resource.Success -> {
                    permintaanPagingState.value = PagingState.Success
                    it.data?.let { list ->
                        permintaanList.addAll(list)
                    }
                }
                null -> { }
            }
        }
    }

    fun loadNextPermintaan() = viewModelScope.launch {
        repository.getNextPermintaanWithNoFilter(
            lastVisiblePermintaanCount = permintaanList.last().permintaan_count ?: 0
        ).collect{
            when(it){
                is Resource.Error -> {
                    permintaanPagingState.value = PagingState.NextLoadError
                }
                is Resource.Loading -> {
                    permintaanPagingState.value = PagingState.NextLoad
                }
                is Resource.Success -> {
                    permintaanPagingState.value = PagingState.Success
                    it.data?.let { list ->
                        if(list.isEmpty()) permintaaanPageFinished.value = true
                        permintaanList.addAll(list)
                    }
                }
                null -> { }
            }
        }
    }

    fun refreshProduct(){
        productList.clear()

        loadFirstProducts()
    }

    fun refreshPermintaan(){
        permintaanList.clear()

        loadFirstPermintaan()
    }

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
        loadFirstProducts()
        loadFirstPermintaan()
    }
}