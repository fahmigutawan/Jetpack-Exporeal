package com.bcc.exporeal.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bcc.exporeal.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val emailState = mutableStateOf("")
    val passwordState = mutableStateOf("")
    val showPassword = mutableStateOf(false)
    val showFillAllFieldsSnackbar = mutableStateOf(false)
    val showPleaseWaitSnackbar = mutableStateOf(false)
    val showErrorSnackbar = mutableStateOf(false)
    val isLoading = mutableStateOf(false)

    fun saveFcmTokenToFirestore(token:String) = repository.saveFcmTokenToFirestore(token)

    fun getFcmToken(onSuccess:(String) -> Unit) = repository.getFcmToken(onSuccess)

    fun login(onSuccess: () -> Unit, onFailed: () -> Unit) {
        isLoading.value = true

        repository.loginWithEmailPassword(
            email = emailState.value,
            password = passwordState.value,
            onSuccess = onSuccess,
            onFailed = onFailed
        )
    }
}
