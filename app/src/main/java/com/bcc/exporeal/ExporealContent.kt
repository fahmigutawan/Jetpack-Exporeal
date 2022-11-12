package com.bcc.exporeal

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.bcc.exporeal.component.AppBottomBar
import com.bcc.exporeal.component.AppTextInputField
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.screen.*
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.viewmodel.MainViewModel
import javax.inject.Inject

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ExporealContent(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    scaffoldState: ScaffoldState,
    repository: AppRepository
) {
    /**Attrs*/

    /**Function*/
    navController.addOnDestinationChangedListener { _, destination, _ ->
        destination.route?.let {
            Log.d("CURRENT ROUTE", it)
            mainViewModel.currentRoute.value = it

            when (it) {
                AppNavRoute.HomeScreen.name -> {
                    mainViewModel.showBottomBar.value = true
                    mainViewModel.showTopBar.value = true
                }

                AppNavRoute.MarketScreen.name -> {
                    mainViewModel.showBottomBar.value = true
                    mainViewModel.showTopBar.value = true
                }

                "${AppNavRoute.MarketScreen.name}/tab={tab}" -> {
                    mainViewModel.showBottomBar.value = true
                    mainViewModel.showTopBar.value = true
                }

                "${AppNavRoute.MarketScreen.name}/tab={tab}/category_id={category_id}" -> {
                    mainViewModel.showBottomBar.value = true
                    mainViewModel.showTopBar.value = true
                }

                AppNavRoute.PelatihanScreen.name -> {
                    mainViewModel.showBottomBar.value = true
                    mainViewModel.showTopBar.value = true
                }

                AppNavRoute.ProfileScreen.name -> {
                    mainViewModel.showBottomBar.value = true
                    mainViewModel.showTopBar.value = false
                }

                else -> {
                    mainViewModel.showBottomBar.value = false
                    mainViewModel.showTopBar.value = false
                }
            }
        }
    }

    /**Content*/
    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = {
            if (mainViewModel.showBottomBar.value) {
                AppBottomBar(
                    onItemClicked = { route ->
                        navController.navigate(route = route)
                    },
                    currentRoute = mainViewModel.currentRoute.value
                )
            }
        },
        topBar = {
            AnimatedVisibility(visible = mainViewModel.showTopBar.value) {
                TopAppBar(backgroundColor = AppColor.Neutral10) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Search
                        AppTextInputField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            placeHolderText = "Cari di Exporeal",
                            valueState = mainViewModel.searchState,
                            endContent = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Icon",
                                    tint = AppColor.Neutral60
                                )
                            })
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 12.dp)
                                .clickable { /*TODO*/ },
                            model = R.drawable.ic_home_notif,
                            contentDescription = "Notif"
                        )

                        AsyncImage(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 12.dp)
                                .clickable { navController.navigate(route = AppNavRoute.ChatListScreen.name) },
                            model = R.drawable.ic_home_chat,
                            contentDescription = "Chat"
                        )
                    }
                }
            }
        }
    ) {
        ExporealNavHost(
            modifier = Modifier.padding(bottom = it.calculateBottomPadding()),
            navController = navController,
            mainViewModel = mainViewModel,
            repository = repository
        )
    }
}

@Composable
fun ExporealNavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    modifier: Modifier,
    repository: AppRepository
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppNavRoute.MySplashScreen.name
    ) {
        composable(route = AppNavRoute.MySplashScreen.name) {
            MySplashScreen(navController = navController)
        }

        composable(route = AppNavRoute.LandingScreen.name) {
            LandingScreen(navController = navController)
        }

        composable(route = AppNavRoute.LoginScreen.name) {
            LoginScreen(navController = navController)
        }

        composable(route = AppNavRoute.RegisterScreen.name) {
            RegisterScreen(navController = navController)
        }

        composable(route = AppNavRoute.HomeScreen.name) {
            HomeScreen(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }

        composable(route = AppNavRoute.MarketScreen.name) {
            MarketScreen(
                navController = navController,
                mainViewModel = mainViewModel
            )
        }

        composable(
            route = "${AppNavRoute.MarketScreen.name}/tab={tab}",
            arguments = listOf(
                navArgument("tab"){ type = NavType.StringType }
            )
        ) {
            val tab = it.arguments?.getString("tab") ?: ""

            MarketScreen(
                navController = navController,
                mainViewModel = mainViewModel,
                tab = tab
            )
        }

        composable(
            route = "${AppNavRoute.MarketScreen.name}/tab={tab}/category_id={category_id}",
            arguments = listOf(
                navArgument("tab"){ type = NavType.StringType },
                navArgument("category_id"){ type = NavType.StringType }
            )
        ) {
            val tab = it.arguments?.getString("tab") ?: ""
            val category_id = it.arguments?.getString("category_id") ?: ""

            MarketScreen(
                navController = navController,
                mainViewModel = mainViewModel,
                tab = tab,
                category_id = category_id
            )
        }

        composable(route = AppNavRoute.PelatihanScreen.name) {

        }

        composable(route = AppNavRoute.ProfileScreen.name) {
            ProfileScreen(navController = navController)
        }

        composable(route = AppNavRoute.BusinessRegistrationScreen.name) {
            BusinessRegisterScreen(navController = navController)
        }

        composable(route = AppNavRoute.ProductDetailScreen.name) {
            ProductDetailScreen(navController = navController, mainViewModel = mainViewModel)
        }

        composable(route = AppNavRoute.PermintaanDetailScreen.name) {
            PermintaanDetailScreen(navController = navController, mainViewModel = mainViewModel)
        }

        composable(route = AppNavRoute.BusinessRegistrationVerificationLandingScreen.name) {
            BusinessVerificationStatusLandingScreen(navController = navController)
        }

        composable(route = AppNavRoute.ChatListScreen.name) {
            ChatListScreen(navController = navController)
        }

        composable(
            route = "${AppNavRoute.ChatDetailScreen.name}/{uid}",
            arguments = listOf(
                navArgument(name = "uid") {
                    type = NavType.StringType
                }
            )
        ) {
            val target_uid = it.arguments?.getString("uid") ?: ""

            ChatDetailScreen(navController = navController, target_uid = target_uid)
        }

        composable(
            route = "${AppNavRoute.ChatDetailScreen.name}/{uid}/prod={product_id}",
            arguments = listOf(
                navArgument(name = "uid") {
                    type = NavType.StringType
                },
                navArgument(name = "product_id") {
                    type = NavType.StringType
                }
            )
        ) {
            val target_uid = it.arguments?.getString("uid") ?: ""
            val product_id = it.arguments?.getString("product_id") ?: ""

            ChatDetailScreen(
                navController = navController,
                target_uid = target_uid,
                product_id = product_id
            )
        }

        composable(
            route = "${AppNavRoute.ChatDetailScreen.name}/{uid}/req={permintaan_id}",
            arguments = listOf(
                navArgument(name = "uid") {
                    type = NavType.StringType
                },
                navArgument(name = "permintaan_id") {
                    type = NavType.StringType
                }
            )
        ) {
            val target_uid = it.arguments?.getString("uid") ?: ""
            val permintaan_id = it.arguments?.getString("permintaan_id") ?: ""

            ChatDetailScreen(
                navController = navController,
                target_uid = target_uid,
                permintaan_id = permintaan_id
            )
        }

        composable(route = AppNavRoute.ProductOfMerchantScreen.name) {
            ProductOfMerchantScreen(navController = navController, mainViewModel = mainViewModel)
        }

        composable(route = AppNavRoute.AddProductScreen.name){
            AddProductScreen(navController = navController)
        }
    }
}