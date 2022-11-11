package com.bcc.exporeal.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcc.exporeal.model.ChatDataModel
import com.bcc.exporeal.model.UserModel
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val listOfChat = mutableStateListOf<ChatDataModel>()

    fun getCurrentUid() = repository.getCurrentUid()

    fun getUserInfoByUid(uid: String, onRetrieved: (Resource<UserModel>?) -> Unit) =
        viewModelScope.launch {
            repository.getUserInfoByUid(uid, 0L).collect {
                onRetrieved(it)
            }
        }

    init {
        listOfChat.clear()

        viewModelScope.launch {
            repository.getListOfChatRoomUid1(repository.getCurrentUid()).collect {
                if (it is Resource.Success) {
                    it.data?.let { list ->
                        listOfChat.addAll(list)
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.getListOfChatRoomUid2(repository.getCurrentUid()).collect {
                if (it is Resource.Success) {
                    it.data?.let { list ->
                        listOfChat.addAll(list)
                    }
                }
            }
        }
    }
}