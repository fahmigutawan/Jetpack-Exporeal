package com.bcc.exporeal.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bcc.exporeal.R
import com.bcc.exporeal.component.AppButton
import com.bcc.exporeal.component.AppText
import com.bcc.exporeal.component.TextType
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.ListenAppBackHandler
import com.bcc.exporeal.viewmodel.LandingViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LandingScreen(navController: NavController) {
    /**Attrs*/
    val viewModel = hiltViewModel<LandingViewModel>()
    val viewPagerState = rememberPagerState()
    val landingImages = listOf(
        R.drawable.ic_landing1, R.drawable.ic_landing2, R.drawable.ic_landing3
    )
    val coroutineScope = rememberCoroutineScope()
    val landingTitles = listOf(
        "Let the world see your business!",
        "Enrich you knowledge through our course!",
        "Register your business now!"
    )
    val landingDescriptions = listOf(
        "Reach your customers all over the world. You can make you products go international by exporting.",
        "Didn't know anything about export? Don't worry, we are here. Course feature allows you to learn a lot of things about export.",
        "Are you ready to expand and grow your business? What are you waiting for? Let's register your business now!."
    )

    /**Function*/
    ListenAppBackHandler {
        when {
            viewPagerState.currentPage > 0 -> coroutineScope.launch {
                viewPagerState.animateScrollToPage(viewPagerState.currentPage - 1)
            }

            else -> navController.popBackStack()
        }
    }

    /**Content*/
    HorizontalPager(count = 3, state = viewPagerState) { index ->
        LandingContent(
            viewModel = viewModel,
            navController = navController,
            imgId = landingImages[index],
            title = landingTitles[index],
            description = landingDescriptions[index],
            state = viewPagerState,
            index = index
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun LandingContent(
    viewModel: LandingViewModel,
    navController: NavController,
    imgId: Int,
    title: String,
    description: String,
    state: PagerState,
    index: Int
) {
    val scrHeight = LocalConfiguration.current.screenHeightDp
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.Blue20)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                model = imgId,
                contentDescription = "Img"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp))
                    .background(AppColor.Neutral10)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = (scrHeight / 2).dp)
                        .padding(24.dp), verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Title and description
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            HorizontalPagerIndicator(
                                pagerState = state,
                                inactiveColor = AppColor.Neutral40,
                                activeColor = AppColor.Blue60
                            )
                        }

                        AppText(
                            text = title, textType = TextType.H2, color = AppColor.Blue100
                        )

                        AppText(
                            text = description,
                            textType = TextType.Body1,
                            color = AppColor.Neutral60
                        )
                    }

                    // Buttons
                    when {
                        index == (state.pageCount - 1) -> {
                            AppButton(
                                modifier = Modifier.fillMaxWidth(), onClick = {
                                    viewModel.saveLandingState(true, onSaved = {
                                        if (viewModel.isLoggedIn()) {
                                            navController.navigate(route = AppNavRoute.HomeScreen.name) {
                                                popUpTo(AppNavRoute.LandingScreen.name) {
                                                    inclusive = true
                                                }
                                            }
                                        } else {
                                            navController.navigate(route = AppNavRoute.LoginScreen.name) {
                                                popUpTo(AppNavRoute.LandingScreen.name) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    })
                                }, text = "GET STARTED!"
                            )
                        }
                        else -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(onClick = {
                                    viewModel.saveLandingState(true, onSaved = {
                                        if (viewModel.isLoggedIn()) {
                                            navController.navigate(route = AppNavRoute.HomeScreen.name) {
                                                popUpTo(AppNavRoute.LandingScreen.name) {
                                                    inclusive = true
                                                }
                                            }
                                        } else {
                                            navController.navigate(route = AppNavRoute.LoginScreen.name) {
                                                popUpTo(AppNavRoute.LandingScreen.name) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    })
                                }) {
                                    AppText(
                                        text = "SKIP",
                                        textType = TextType.ButtonNormal,
                                        color = AppColor.Blue60
                                    )
                                }

                                AppButton(onClick = {
                                    coroutineScope.launch {
                                        state.animateScrollToPage(state.currentPage + 1)
                                    }
                                }, text = "NEXT")
                            }
                        }
                    }
                }
            }
        }
    }
}