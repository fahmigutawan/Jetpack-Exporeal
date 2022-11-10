package com.bcc.exporeal.screen

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bcc.exporeal.SnackbarListener
import com.bcc.exporeal.component.*
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.getFileName
import com.bcc.exporeal.viewmodel.BusinessRegisterViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BusinessRegisterScreen(navController: NavController) {
    /**Attrs*/
    val viewModel = hiltViewModel<BusinessRegisterViewModel>()
    val docPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            it?.let {
                viewModel.pickedDocumentUri.value = it
            }
        }
    )
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = viewModel.isLoading.value)

    /**Function*/
    if (viewModel.isLoading.value) {
        LaunchedEffect(key1 = true) {
            delay(2000)
            viewModel.isLoading.value = false
        }
    }
    SnackbarListener("Pick your document first", viewModel.showPickDocumentSnackbar)
    SnackbarListener("Fill all fields before continue", viewModel.showFillAllFieldsSnackbar)
    SnackbarListener("Something went wrong. Try again later", viewModel.showFailedSnackbar)

    /**Content*/
    SwipeRefresh(state = swipeRefreshState, onRefresh = { /*TODO*/ }) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onBackClicked = { navController.popBackStack() },
                    title = "Pendaftaran Bisnis"
                )
            }
        ) {
            BusinessRegisterContent(
                viewModel = viewModel,
                navController = navController,
                docPicker = docPicker
            )
        }
    }
}

@Composable
private fun BusinessRegisterContent(
    viewModel: BusinessRegisterViewModel,
    navController: NavController,
    docPicker: ManagedActivityResultLauncher<String, Uri?>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // title
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                AppText(text = "Business Registration", textType = TextType.H3)
                AppText(
                    text = "Fill in the following data corrctly to register you business",
                    textType = TextType.Body1,
                    color = AppColor.Neutral60
                )
            }
        }

        // Business name
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeHolderText = "Business Name",
                valueState = viewModel.businessNameState
            )
        }

        // Business owner
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeHolderText = "Business Owner",
                valueState = viewModel.businessOwnerState
            )
        }

        // Business description
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .padding(horizontal = 16.dp),
                placeHolderText = "Business Description",
                valueState = viewModel.businessDescriptionState,
                singleLine = false
            )
        }

        // Contact title
        item {
            Box(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                AppText(text = "Contact", textType = TextType.H3)
            }
        }

        // Contact phone number
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeHolderText = "Phone Number",
                valueState = viewModel.contactPhoneNumState
            )
        }

        // Contact email
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeHolderText = "Email",
                valueState = viewModel.contactEmailState
            )
        }

        // Address title
        item {
            Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                AppText(text = "Address", textType = TextType.H3)
                AppText(
                    text = "Fill in the following data below according to where your business operates",
                    textType = TextType.Body1,
                    color = AppColor.Neutral60
                )
            }
        }

        // Address details
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeHolderText = "Address Details",
                valueState = viewModel.addressDetailState
            )
        }

        // Address country
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeHolderText = "Country",
                valueState = viewModel.addressCountryState
            )
        }

        // Address province
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeHolderText = "State/Province",
                valueState = viewModel.addressProvinceState
            )
        }

        // Address city
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeHolderText = "City",
                valueState = viewModel.addressCityState
            )
        }

        // Address postal
        item {
            AppTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeHolderText = "Postal Code",
                valueState = viewModel.addressPostalCodeState
            )
        }

        // Business document title
        item {
            Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                AppText(text = "Business Document", textType = TextType.H3)
                AppText(
                    text = "Make sure the document you upoad is a valid document",
                    textType = TextType.Body1,
                    color = AppColor.Neutral60
                )
            }
        }

        // Picked document
        if (viewModel.pickedDocumentUri.value != null) {
            item {
                val context = LocalContext.current
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppText(text = "Picked document:", textType = TextType.Body3Semibold)
                    AppText(
                        text = context.getFileName(viewModel.pickedDocumentUri.value!!),
                        textType = TextType.Body3
                    )
                }
            }
        }

        // Business document picker
        item {
            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                    docPicker.launch("*/*")
                },
                backgroundColor = AppColor.Blue10,
                rippleColor = AppColor.Blue60,
                borderWidth = 1.dp,
                borderColor = AppColor.Blue60
            ) {
                AppText(
                    text = "Nomor Induk Berusaha (NIB)",
                    textType = TextType.Body1,
                    color = AppColor.Neutral50
                )
            }
        }

        // Buttons
        item {
            Column(
                Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (viewModel.pickedDocumentUri.value == null) {
                            viewModel.showPickDocumentSnackbar.value = true
                        } else viewModel.isLoading.value = true
                    },
                    text = "UPLOAD DOCUMENT",
                    textColor = AppColor.Neutral100,
                    backgroundColor = AppColor.Neutral10,
                    borderColor = AppColor.Blue60,
                    borderWidth = 1.dp
                )

                AppButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        when {
                            viewModel.pickedDocumentUri.value == null -> {
                                viewModel.showPickDocumentSnackbar.value = true
                            }
                            !viewModel.isAllFieldFilled() -> {
                                viewModel.showFillAllFieldsSnackbar.value = true
                            }
                            else -> {
                                viewModel.isLoading.value = true
                                viewModel.saveBusinessRegistration(
                                    onFailed = {
                                        viewModel.showFailedSnackbar.value = true
                                    },
                                    onSuccess = {
                                        navController.navigate(route = AppNavRoute.BusinessRegistrationVerificationLandingScreen.name) {
                                            popUpTo(route = AppNavRoute.BusinessRegistrationScreen.name) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    },
                    text = "SEND"
                )
            }
        }
    }
}