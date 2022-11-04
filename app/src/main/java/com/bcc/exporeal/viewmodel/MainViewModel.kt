package com.bcc.exporeal.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo:AppRepository
) :ViewModel() {
    val showBottomBar = mutableStateOf(false)
    val currentRoute = mutableStateOf(AppNavRoute.MySplashScreen.name)
}