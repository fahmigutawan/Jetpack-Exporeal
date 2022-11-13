package com.bcc.exporeal.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcc.exporeal.model.AddressModel
import com.bcc.exporeal.model.UserModel
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.screen.OrderDetailScreenState
import com.bcc.exporeal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val pickedAddress = mutableStateOf<AddressModel?>(null)
    val pickedQuantity = mutableStateOf(1)
    val subTotalProduct = mutableStateOf(0)
    val deliveryPerUnit = mutableStateOf(70000)
    val subTotalDelivery = mutableStateOf(pickedQuantity.value * deliveryPerUnit.value)
    val total = mutableStateOf(subTotalProduct.value + subTotalDelivery.value)
    val orderDetailScreenState = mutableStateOf(OrderDetailScreenState.Main)
    val addAddressState = mutableStateOf("")
    val showSnackbar = mutableStateOf(false)
    val snackbarMessage = mutableStateOf("")
    val isLoading = mutableStateOf(false)

    private val _address = MutableStateFlow<Resource<List<AddressModel>>?>(Resource.Loading())
    val address get() = _address

    private val _userInfo = MutableStateFlow<Resource<UserModel>?>(Resource.Loading())
    val userInfo get() = _userInfo

    private val _merchantInfo = MutableStateFlow<Resource<UserModel>?>(Resource.Loading())
    val merchantInfo get() = _merchantInfo

    fun getAddress() = viewModelScope.launch {
        repository.getAddresses().collect {
            _address.value = it
            if (it is Resource.Loading) isLoading.value = true
        }
    }

    fun getUserInfo() = viewModelScope.launch {
        repository.getOwnUserInfo().collect {
            _userInfo.value = it
            if (it is Resource.Loading) isLoading.value = true
        }
    }

    fun getMerchantInfo(uid: String) = viewModelScope.launch {
        repository.getUserInfoByUid(uid).collect {
            _merchantInfo.value = it
        }
    }

    fun saveAddress(address: String, onSuccess: (AddressModel) -> Unit, onFailed: () -> Unit) =
        repository.saveAddress(address, onSuccess, onFailed)

    fun refresh() {
        getAddress()
        getUserInfo()
    }

    init {
        getAddress()
        getUserInfo()
    }
}