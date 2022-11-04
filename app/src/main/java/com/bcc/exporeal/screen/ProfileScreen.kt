package com.bcc.exporeal.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bcc.exporeal.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    /**Attrs*/
    val viewModel = hiltViewModel<ProfileViewModel>()

    /**Function*/

    /**Content*/
}

@Composable
private fun ProfileContent(navController: NavController, viewModel: ProfileViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize()){
        // Profile Info
    }
}