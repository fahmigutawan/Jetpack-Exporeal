package com.bcc.exporeal.screen

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.bcc.exporeal.component.AppTopBar

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BusinessRegisterScreen(navController: NavController) {
    /**Attrs*/

    /**Function*/

    /**Content*/
    Scaffold(
        topBar = {
            AppTopBar(
                onBackClicked = { navController.popBackStack() },
                title = "Pendaftaran Bisnis"
            )
        }
    ) {

    }
}