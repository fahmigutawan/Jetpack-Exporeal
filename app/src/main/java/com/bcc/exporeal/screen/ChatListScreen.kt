package com.bcc.exporeal.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bcc.exporeal.component.AppButton
import com.bcc.exporeal.component.AppText
import com.bcc.exporeal.component.AppTopBar
import com.bcc.exporeal.component.TextType
import com.bcc.exporeal.model.UserModel
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.Resource
import com.bcc.exporeal.viewmodel.ChatListViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChatListScreen(
    navController: NavController
) {
    /**Attrs*/
    val viewModel = hiltViewModel<ChatListViewModel>()

    /**Function*/

    /**Content*/
    Scaffold(
        topBar = {
            AppTopBar(onBackClicked = { navController.popBackStack() }, title = "Chat")
        }
    ) {
        ChatListContent(navController = navController, viewModel = viewModel)
    }
}

@Composable
private fun ChatListContent(
    navController: NavController,
    viewModel: ChatListViewModel
) {
    LazyColumn(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if(viewModel.listOfChat.isEmpty()){
            item{
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                    AppText(text = "You don't have any conversations", textType = TextType.Body3)
                }
            }
        }

        items(viewModel.listOfChat.toList()) { item ->
            val user = remember { mutableStateOf<Resource<UserModel>?>(Resource.Loading()) }

            if (user.value !is Resource.Success) {
                LaunchedEffect(key1 = true) {
                    viewModel.getUserInfoByUid(
                        uid = if ((item.uid_1 ?: "") == viewModel.getCurrentUid()) item.uid_2 ?: ""
                        else item.uid_1 ?: "",
                        onRetrieved = {
                            user.value = it
                        }
                    )
                }
            }

            // Content
            when (user.value) {
                is Resource.Error -> {

                }
                is Resource.Loading -> {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .placeholder(
                                    visible = true,
                                    color = AppColor.Neutral50,
                                    shape = CircleShape,
                                    highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20)
                                )
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            AppText(
                                modifier = Modifier.placeholder(
                                    visible = true,
                                    color = AppColor.Neutral50,
                                    shape = RoundedCornerShape(4.dp),
                                    highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20)
                                ),
                                text = "Target Name",
                                textType = TextType.Body3
                            )
                            AppText(
                                modifier = Modifier.placeholder(
                                    visible = true,
                                    color = AppColor.Neutral50,
                                    shape = RoundedCornerShape(4.dp),
                                    highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20)
                                ),
                                text = item.last_chat ?: "",
                                textType = TextType.Body3,
                                color = AppColor.Neutral50
                            )
                        }
                    }
                }
                is Resource.Success -> {
                    user.value?.data?.let {
                        Box(
                            modifier = Modifier.clickable {
                                if ((item.uid_1 ?: "") == viewModel.getCurrentUid()) {
                                    navController.navigate(route = "${AppNavRoute.ChatDetailScreen.name}/${item.uid_2}")
                                } else {
                                    navController.navigate(route = "${AppNavRoute.ChatDetailScreen.name}/${item.uid_1}")
                                }
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                AsyncImage(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    model = it.profile_pic ?: "",
                                    contentDescription = "Img"
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    AppText(text = it.name ?: "", textType = TextType.Body1Semibold)
                                    AppText(
                                        text = item.last_chat ?: "",
                                        textType = TextType.Body2,
                                        color = AppColor.Neutral50
                                    )
                                }
                            }
                        }
                    }
                }
                null -> {}
            }
        }
    }
}