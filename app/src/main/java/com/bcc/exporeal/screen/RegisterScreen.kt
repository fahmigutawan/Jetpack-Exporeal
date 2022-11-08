package com.bcc.exporeal.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
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
import com.bcc.exporeal.util.ListenAppBackHandler
import com.bcc.exporeal.viewmodel.RegisterViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegisterScreen(navController: NavController) {
    /**Attrs*/
    val viewModel = hiltViewModel<RegisterViewModel>()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isLoading.value)

    /**Function*/
    ListenAppBackHandler {
        when{
            viewModel.isLoading.value -> viewModel.showPleaseWaitSnackbar.value = true
            else -> {
                when (viewModel.registerStep.value) {
                    1 -> navController.popBackStack()
                    2 -> viewModel.registerStep.value = 1
                }
            }
        }
    }
    SnackbarListener(
        "Pastikan semua field telah terisi",
        viewModel.showFillAllFieldsSnackbar
    )
    SnackbarListener(
        "Harap tunggu",
        viewModel.showPleaseWaitSnackbar
    )
    SnackbarListener(
        "Registrasi gagal, coba lagi nanti",
        viewModel.showErrorSnackbar
    )

    /**Content*/
    SwipeRefresh(state = swipeRefreshState, onRefresh = { /*TODO*/ }) {
        when (viewModel.registerStep.value) {
            1 -> {
                RegisterContent(
                    viewModel = viewModel,
                    navController = navController
                )
            }

            2 -> {
                Scaffold(
                    topBar = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Box(modifier = Modifier.padding(16.dp)) {
                                AppTextButton(
                                    text = "KEMBALI",
                                    textType = TextType.ButtonNormal,
                                    onClick = { viewModel.registerStep.value = 1 },
                                    color = AppColor.Blue60
                                )
                            }
                        }
                    }
                ) {
                    RegisterAdvanceContent(
                        viewModel = viewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisterContent(
    viewModel: RegisterViewModel,
    navController: NavController
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
                    text = "Buat akun", textType = TextType.H1
                )
                AppText(
                    text = "Daftarkan akun Anda untuk mengakses semua fitur dalam aplikasi",
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
                        text = "Daftar dengan Google",
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
            AppTextInputField(placeHolderText = "Email", valueState = viewModel.emailState)
        }

        // Selanjutnya btn
        item {
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    when {
                        viewModel.emailState.value.isEmpty() -> {
                            viewModel.showFillAllFieldsSnackbar.value = true
                        }

                        else -> {
                            viewModel.registerStep.value = 2
                        }
                    }
                },
                text = "SELANJUTNYA"
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
                        text = "Sudah punya akun?",
                        textType = TextType.Body2,
                        color = AppColor.Neutral50
                    )
                    AppTextButton(
                        text = "Masuk",
                        textType = TextType.Body2Semibold,
                        color = AppColor.Blue60,
                        onClick = { navController.navigate(route = AppNavRoute.LoginScreen.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisterAdvanceContent(
    viewModel: RegisterViewModel,
    navController: NavController
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
                    text = "Isi data diri", textType = TextType.H1
                )
                AppText(
                    text = "Satu langkah lagi! Isi data berikut untuk membuat akun",
                    textType = TextType.Body2
                )
            }
        }

        // Nama field
        item {
            AppTextInputField(placeHolderText = "Nama Lengkap", valueState = viewModel.fullNameState)
        }

        // No HP field
        item {
            AppTextInputField(placeHolderText = "Nomor HP", valueState = viewModel.mobileNumState)
        }

        // Password field & SK
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

                AppText(
                    text = "Dengan melanjutkan, Anda menyetujui Ketentuan Layanan dan Kebijakan Exporeal",
                    textType = TextType.Body3,
                    color = AppColor.Neutral50
                )
            }
        }

        // Daftar btn
        item {
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    when {
                        viewModel.emailState.value.isEmpty() -> {
                            viewModel.showFillAllFieldsSnackbar.value = true
                        }

                        viewModel.fullNameState.value.isEmpty() -> {
                            viewModel.showFillAllFieldsSnackbar.value = true
                        }

                        viewModel.mobileNumState.value.isEmpty() -> {
                            viewModel.showFillAllFieldsSnackbar.value = true
                        }

                        viewModel.passwordState.value.isEmpty() -> {
                            viewModel.showFillAllFieldsSnackbar.value = true
                        }

                        viewModel.isLoading.value -> {
                            viewModel.showPleaseWaitSnackbar.value = true
                        }

                        else -> {
                            viewModel.register(
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
                text = "DAFTAR"
            )
        }
    }
}