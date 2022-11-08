package com.bcc.exporeal.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcc.exporeal.model.UserModel
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val isLoading = mutableStateOf(false)
    val pickedImageUri = mutableStateOf<Uri?>(null)
    val showErrorChangeProfilePicSnackbar = mutableStateOf(false)
    val showSuccessChangeProfilePicSnackbar = mutableStateOf(false)
    val showUploadProgressDialog = mutableStateOf(false)
    val profilePictureTotalProgress = mutableStateOf(1L)
    val profilePictureTransferredProgress = mutableStateOf(0L)

    fun logout(afterDelay: () -> Unit) = viewModelScope.launch {
        repository.logout(afterDelay = afterDelay)
    }

    fun uploadProfilePicture(
        uri: Uri,
        user: UserModel,
        onSuccess: () -> Unit,
        onFailed: () -> Unit,
        onProgress: (transferred: Long, total: Long) -> Unit
    ) = repository.saveProfilePicture(
        uri, user, onSuccess, onFailed, onProgress
    )

    private val _user = MutableStateFlow<Resource<UserModel>?>(Resource.Loading())
    val user get() = _user

    init {
        viewModelScope.launch {
            repository.getOwnUserInfo().collect {
                _user.value = it
            }
        }
    }
}