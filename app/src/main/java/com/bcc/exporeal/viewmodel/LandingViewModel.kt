package com.bcc.exporeal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcc.exporeal.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val repository: AppRepository
):ViewModel() {
    fun saveLandingState(hasPassed: Boolean, onSaved:() -> Unit) = viewModelScope.launch {
        repository.savePassingLandingScreenState(hasPassed)
        delay(2000)
        onSaved()
    }

    fun isLoggedIn() = repository.isLoggedIn()
}