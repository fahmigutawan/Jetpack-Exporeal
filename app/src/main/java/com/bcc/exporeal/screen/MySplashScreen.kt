package com.bcc.exporeal.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bcc.exporeal.R
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.viewmodel.MySplashViewModel
import kotlinx.coroutines.delay

@Composable
fun MySplashScreen(
    navController: NavController
) {
    /**Attrs*/
    val viewModel = hiltViewModel<MySplashViewModel>()

    /**Function*/
    LaunchedEffect(key1 = true) {
        delay(3000)
        viewModel.hasPassedLandingScreen().collect { hasPassed ->
            if (hasPassed) {
                if(viewModel.isLoggedIn()){
                    navController.navigate(route = AppNavRoute.HomeScreen.name) {
                        popUpTo(route = AppNavRoute.MySplashScreen.name) {
                            inclusive = true
                        }
                    }
                }else{
                    navController.navigate(route = AppNavRoute.LoginScreen.name) {
                        popUpTo(route = AppNavRoute.MySplashScreen.name) {
                            inclusive = true
                        }
                    }
                }
            } else {
                navController.navigate(route = AppNavRoute.LandingScreen.name) {
                    popUpTo(route = AppNavRoute.MySplashScreen.name) {
                        inclusive = true
                    }
                }
            }
        }
    }

    /**Content*/
    Box(
        modifier = Modifier
            .background(AppColor.Neutral10)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier.padding(horizontal = 32.dp),
            model = R.drawable.ic_logo,
            contentDescription = "Logo",
            contentScale = ContentScale.FillWidth
        )
    }
}