package com.bcc.exporeal.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcc.exporeal.model.ProductModel
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.util.PagingState
import com.bcc.exporeal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductOfMerchantViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val searchState = mutableStateOf("")
    val productList = mutableStateListOf<ProductModel>()
    val pagingState = mutableStateOf(PagingState.Success)
    val pageFinished = mutableStateOf(false)

    fun loadFirstProducts() = viewModelScope.launch {
        repository.getMyFirstProducts().collect{
            when(it){
                is Resource.Error -> {
                    pagingState.value = PagingState.NextLoadError
                }
                is Resource.Loading -> {
                    pagingState.value = PagingState.NextLoad
                }
                is Resource.Success -> {
                    pagingState.value = PagingState.Success
                    it.data?.let { list ->
                        productList.addAll(list)
                    }
                }
                null -> {}
            }
        }
    }

    fun loadNextProducts() = viewModelScope.launch {
        repository.getMyNextProducts(
            lastVisibleCount = productList.last().product_count ?: 0
        ).collect{
            when(it){
                is Resource.Error -> {
                    pagingState.value = PagingState.NextLoadError
                }
                is Resource.Loading -> {
                    pagingState.value = PagingState.NextLoad
                }
                is Resource.Success -> {
                    pagingState.value = PagingState.Success
                    it.data?.let { list ->
                        if(list.isNotEmpty()) pageFinished.value = true
                        productList.addAll(list)
                    }
                }
                null -> { }
            }
        }
    }

    init {
        loadFirstProducts()
    }
}