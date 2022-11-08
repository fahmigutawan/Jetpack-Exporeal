package com.bcc.exporeal.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.TextButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bcc.exporeal.R
import com.bcc.exporeal.SnackbarListener
import com.bcc.exporeal.component.*
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.viewmodel.LoginViewModel
import com.bcc.exporeal.viewmodel.RegisterViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun LoginScreen(navController: NavController) {
    /**Attrs*/
    val viewModel = hiltViewModel<LoginViewModel>()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isLoading.value)

    /**Function*/
    SnackbarListener(
        "Pastikan semua data telah terisi",
        viewModel.showFillAllFieldsSnackbar
    )
    SnackbarListener(
        "Harap tunggu",
        viewModel.showPleaseWaitSnackbar
    )
    SnackbarListener(
        "Pastikan email anda terdaftar dan terhubung ke internet",
        viewModel.showErrorSnackbar
    )

    /**Content*/
    SwipeRefresh(state = swipeRefreshState, onRefresh = { /*TODO*/ }) {
        LoginContent(
            viewModel = viewModel,
            navController = navController
        )
    }
}

@Composable
private fun LoginContent(
    viewModel: LoginViewModel, navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Logo
        item {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp, vertical = 64.dp),
                contentScale = ContentScale.FillWidth,
                model = R.drawable.ic_logo,
                contentDescription = "Logo"
            )
        }

        // Title & Description
        item {
            Column {
                AppText(
                    text = "Selamat datang", textType = TextType.H1
                )
                AppText(
                    text = "Masuk ke akun Anda untuk melanjutkan",
                    textType = TextType.Body2
                )
            }
        }

        // Google btn
        item {
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /*TODO*/ },
                backgroundColor = AppColor.Neutral10,
                borderWidth = 1.dp,
                borderColor = AppColor.Neutral50,
                rippleColor = AppColor.Neutral100
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AsyncImage(
                        modifier = Modifier.size(24.dp),
                        model = R.drawable.ic_google,
                        contentDescription = "Icon Google"
                    )

                    AppText(
                        text = "Masuk dengan Google",
                        textType = TextType.Body1,
                        color = AppColor.Neutral100
                    )
                }
            }
        }

        // "Atau"
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                AppText(text = "atau", textType = TextType.Body2, color = AppColor.Neutral50)
            }
        }

        // Email field
        item {
            AppTextInputField(
                placeHolderText = "Email", valueState = viewModel.emailState
            )
        }

        // Password field & Lupa password
        item {
            Column {
                AppTextInputField(placeHolderText = "Password",
                    valueState = viewModel.passwordState,
                    visualTransformation = when {
                        viewModel.showPassword.value -> VisualTransformation.None
                        else -> PasswordVisualTransformation()
                    },
                    endContent = {
                        AsyncImage(
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    viewModel.showPassword.value = !viewModel.showPassword.value
                                }, model = when {
                                viewModel.showPassword.value -> R.drawable.ic_visible_off
                                else -> R.drawable.ic_visible_on
                            }, contentDescription = "Visible"
                        )
                    })

                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd
                ) {
                    AppTextButton(
                        text = "Lupa password?",
                        textType = TextType.Body2Semibold,
                        color = AppColor.Blue60,
                        onClick = { /*TODO*/ }
                    )
                }
            }
        }

        // Masuk btn
        item {
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    when {
                        viewModel.emailState.value.isEmpty() -> {
                            viewModel.showFillAllFieldsSnackbar.value = true
                        }

                        viewModel.passwordState.value.isEmpty() -> {
                            viewModel.showFillAllFieldsSnackbar.value = true
                        }

                        viewModel.isLoading.value -> {
                            viewModel.showPleaseWaitSnackbar.value = true
                        }

                        else -> {
                            viewModel.login(
                                onSuccess = {
                                    viewModel.isLoading.value = false
                                    navController.navigate(route = AppNavRoute.HomeScreen.name){
                                        navController.backQueue.clear()
                                    }
                                },
                                onFailed = {
                                    viewModel.isLoading.value = false
                                    viewModel.showErrorSnackbar.value = true
                                }
                            )
                        }
                    }
                },
                text = "MASUK"
            )
        }

        // Belum punya akun, daftar
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AppText(
                        text = "Belum punya akun?",
                        textType = TextType.Body2,
                        color = AppColor.Neutral50
                    )
                    AppTextButton(
                        text = "Daftar",
                        textType = TextType.Body2Semibold,
                        color = AppColor.Blue60,
                        onClick = { navController.navigate(route = AppNavRoute.RegisterScreen.name) }
                    )
                }
            }
        }
    }
}