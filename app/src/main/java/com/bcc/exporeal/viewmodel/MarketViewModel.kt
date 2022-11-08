package com.bcc.exporeal.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.screen.MarketTopMenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val repository: AppRepository
):ViewModel() {
    val selectedTopMenu = mutableStateOf(MarketTopMenuItem.Produk)
    val searchState = mutableStateOf("")
}