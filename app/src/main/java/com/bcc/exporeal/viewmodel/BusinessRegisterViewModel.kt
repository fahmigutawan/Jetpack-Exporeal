package com.bcc.exporeal.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bcc.exporeal.model.BusinessModel
import com.bcc.exporeal.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BusinessRegisterViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val isLoading = mutableStateOf(false)
    val businessNameState = mutableStateOf("")
    val businessOwnerState = mutableStateOf("")
    val businessDescriptionState = mutableStateOf("")
    val contactPhoneNumState = mutableStateOf("")
    val contactEmailState = mutableStateOf("")
    val addressDetailState = mutableStateOf("")
    val addressCountryState = mutableStateOf("")
    val addressProvinceState = mutableStateOf("")
    val addressCityState = mutableStateOf("")
    val addressPostalCodeState = mutableStateOf("")
    val pickedDocumentUri = mutableStateOf<Uri?>(null)
    val showPickDocumentSnackbar = mutableStateOf(false)
    val showFillAllFieldsSnackbar = mutableStateOf(false)
    val showFailedSnackbar = mutableStateOf(false)

    fun isAllFieldFilled(): Boolean {
        when {
            businessNameState.value.isEmpty() -> return false
            businessOwnerState.value.isEmpty() -> return false
            businessDescriptionState.value.isEmpty() -> return false
            contactPhoneNumState.value.isEmpty() -> return false
            contactEmailState.value.isEmpty() -> return false
            addressDetailState.value.isEmpty() -> return false
            addressCountryState.value.isEmpty() -> return false
            addressProvinceState.value.isEmpty() -> return false
            addressCityState.value.isEmpty() -> return false
            addressPostalCodeState.value.isEmpty() -> return false
        }

        return true
    }

    fun saveBusinessRegistration(
        onSuccess:() -> Unit,
        onFailed:() -> Unit
    ){
        repository.saveBusinessRegistorToFirestore(
            body = BusinessModel(
                business_id = repository.getRandomKey("business"),
                uid = repository.getCurrentUid(),
                business_name = businessNameState.value,
                business_owner = businessOwnerState.value,
                business_description = businessDescriptionState.value,
                phone_num = contactPhoneNumState.value,
                email = contactEmailState.value,
                address_detail = addressDetailState.value,
                address_country = addressCountryState.value,
                address_province = addressProvinceState.value,
                address_city = addressCityState.value,
                address_postcode = addressPostalCodeState.value,
                verification_status = 1
            ),
            onSuccess = onSuccess,
            onFailed = onFailed
        )
    }
}