package com.bcc.exporeal.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcc.exporeal.model.PermintaanModel
import com.bcc.exporeal.model.ProductModel
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
    val searchState = mutableStateOf("")
    val productPagingState = mutableStateOf(PagingState.Success)
    val permintaanPagingState = mutableStateOf(PagingState.Success)
    val productList = mutableStateListOf<ProductModel>()
    val permintaanList = mutableStateListOf<PermintaanModel>()

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
            lastVisiblePostCount = productList.last().product_count ?: ""
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
            lastVisiblePermintaanCount = permintaanList.last().permintaan_count ?: ""
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

    init {
        loadFirstProducts()
        loadFirstPermintaan()
    }
}