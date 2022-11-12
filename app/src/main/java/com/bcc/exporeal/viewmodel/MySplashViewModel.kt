package com.bcc.exporeal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcc.exporeal.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MySplashViewModel @Inject constructor(
    private val repo:AppRepository
) :ViewModel(){
    fun hasPassedLandingScreen() = repo.hasPassedLandingScreen

    fun isLoggedIn() = repo.isLoggedIn()

    fun isNewFcmTokenAvailable() = repo.isNewFcmTokenAvailable

    fun fcmToken() = repo.fcmToken

    fun saveFcmTokenToFirestore(token:String) = repo.saveFcmTokenToFirestore(token)

    fun getFcmToken(onSuccess:(String) -> Unit) = repo.getFcmToken(onSuccess)

    fun sendNotification(
        my_name: String,
        my_message: String,
        target_token: String
    ) = viewModelScope.launch {
        repo.sendCloudNotification(my_name, my_message, target_token)
    }
}