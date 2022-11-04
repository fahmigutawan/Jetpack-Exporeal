package com.bcc.exporeal.viewmodel

import androidx.lifecycle.ViewModel
import com.bcc.exporeal.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AppRepository
):ViewModel() {
}