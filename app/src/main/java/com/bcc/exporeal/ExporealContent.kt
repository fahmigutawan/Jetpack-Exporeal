package com.bcc.exporeal

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bcc.exporeal.component.AppBottomBar
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.screen.LandingScreen
import com.bcc.exporeal.screen.LoginScreen
import com.bcc.exporeal.screen.RegisterScreen
import com.bcc.exporeal.screen.MySplashScreen
import com.bcc.exporeal.viewmodel.MainViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ExporealContent(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    scaffoldState: ScaffoldState
) {
    /**Attrs*/

    /**Function*/
    navController.addOnDestinationChangedListener{ _,destination,_ ->
        destination.route?.let {
            Log.e("CURRENT ROUTE", it)
            mainViewModel.currentRoute.value = it

            when(it){
                AppNavRoute.HomeScreen.name -> {
                    mainViewModel.showBottomBar.value = true
                }

                AppNavRoute.MarketScreen.name ->{
                    mainViewModel.showBottomBar.value = true
                }

                AppNavRoute.PelatihanScreen.name -> {
                    mainViewModel.showBottomBar.value = true
                }

                AppNavRoute.ProfileScreen.name -> {
                    mainViewModel.showBottomBar.value = true
                }

                else -> {
                    mainViewModel.showBottomBar.value = false
                }
            }
        }
    }

    /**Content*/
    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            if(mainViewModel.showBottomBar.value){
                AppBottomBar(
                    onItemClicked = { route ->
                        navController.navigate(route = route)
                    },
                    currentRoute = mainViewModel.currentRoute.value
                )
            }
        }
    ) {
        ExporealNavHost(navController = navController, mainViewModel = mainViewModel)
    }
}

@Composable
fun ExporealNavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = AppNavRoute.MySplashScreen.name){
        composable(route = AppNavRoute.MySplashScreen.name){
            MySplashScreen(navController = navController)
        }

        composable(route = AppNavRoute.LandingScreen.name){
            LandingScreen(navController = navController)
        }

        composable(route = AppNavRoute.LoginScreen.name){
            LoginScreen(navController = navController)
        }

        composable(route = AppNavRoute.RegisterScreen.name){
            RegisterScreen(navController = navController)
        }

        composable(route = AppNavRoute.HomeScreen.name){

        }

        composable(route = AppNavRoute.MarketScreen.name){

        }

        composable(route = AppNavRoute.PelatihanScreen.name){

        }

        composable(route = AppNavRoute.ProfileScreen.name){

        }
    }
}