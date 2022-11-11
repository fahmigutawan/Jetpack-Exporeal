package com.bcc.exporeal.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bcc.exporeal.R
import com.bcc.exporeal.component.AppButton
import com.bcc.exporeal.component.AppText
import com.bcc.exporeal.component.TextType
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.ListenAppBackHandler

@Composable
fun BusinessVerificationStatusLandingScreen(
    navController: NavController
) {
    val imgWidth = LocalConfiguration.current.screenWidthDp / 3
    ListenAppBackHandler {
        navController.navigate(route = AppNavRoute.ProfileScreen.name){
            popUpTo(route = AppNavRoute.BusinessRegistrationVerificationLandingScreen.name){
                inclusive = true
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(64.dp),
                model = R.drawable.ic_registration_business_landing,
                contentDescription = "Business logo"
            )

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                AppText(text = "Your registration is in progress...", textType = TextType.H3)
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                AppText(
                    text = "Please wait for a certain period of time while your document being verified by our team",
                    textType = TextType.Body2,
                    color = AppColor.Neutral50
                )
            }
        }

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(route = AppNavRoute.ProfileScreen.name){
                    popUpTo(route = AppNavRoute.BusinessRegistrationVerificationLandingScreen.name){
                        inclusive = true
                    }
                }
            },
            text = "BACK TO PROFILE SCREEN",
            textColor = AppColor.Blue60,
            backgroundColor = AppColor.Blue10,
            borderWidth = 1.dp,
            borderColor = AppColor.Blue60
        )
    }
}