package com.bcc.exporeal.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bcc.exporeal.R
import com.bcc.exporeal.component.*
import com.bcc.exporeal.model.ChatItemModel
import com.bcc.exporeal.model.UserModel
import com.bcc.exporeal.ui.style.AppColor
import com.bcc.exporeal.util.Resource
import com.bcc.exporeal.viewmodel.ChatDetailViewModel
import com.bcc.exporeal.viewmodel.MainViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChatDetailScreen(
    navController: NavController,
    target_uid: String,
    product_id: String = "",
    permintaan_id: String = "",
    mainViewModel: MainViewModel
) {

    /**Attrs*/
    val viewModel = hiltViewModel<ChatDetailViewModel>()
    val targetUser = viewModel.target_user.collectAsState()
    val user = viewModel.user.collectAsState()
    val density = LocalDensity.current
    val swipeRefreshState =
        rememberSwipeRefreshState(isRefreshing = viewModel.chatRoomSnapshot.value == null)

    /**Function*/
    if (viewModel.shouldLoadId.value) {
        LaunchedEffect(key1 = true) {
            if (product_id.isNotEmpty()) {
                viewModel.withItem.value = product_id
                viewModel.shouldLoadId.value = false
            }
            if (permintaan_id.isNotEmpty()) {
                viewModel.withItem.value = permintaan_id
                viewModel.shouldLoadId.value = false
            }
        }
    }
    if (targetUser.value is Resource.Loading) {
        LaunchedEffect(key1 = true) {
            viewModel.getTargetUserInfo(target_uid)
        }
    }
    if (!viewModel.isChatRoomCheckLoaded.value) {
        LaunchedEffect(key1 = true) {
            viewModel.getAvailableChatRoom(possibleChannel1 = "${viewModel.getCurrentUid()}-${target_uid}",
                possibleChannel2 = "${target_uid}-${viewModel.getCurrentUid()}",
                onSuccess = { shouldCreateNew, channel ->
                    if (shouldCreateNew) {
                        viewModel.shouldCreateNew.value = true
                        viewModel.chatChannelId.value = channel
                    } else {
                        viewModel.isChatRoomCheckLoaded.value = true
                        viewModel.chatChannelId.value = channel
                    }
                },
                onFailed = {
                    /*TODO*/
                })
        }
    }
    if (viewModel.chatChannelId.value.isNotEmpty()) {
        viewModel.listenToChatByChannelId(
            viewModel.chatChannelId.value,
            onDataChange = {
                Log.e("SOME DATA CHANGE", "IDK")
                viewModel.chatRoomSnapshot.value = it
            },
            onCancelled = {
                /*TODO*/
            }
        )
    }

    /**Content*/
    Scaffold(
        topBar = {
            val imgSize = remember { mutableStateOf(0.dp) }
            TopAppBar(
                backgroundColor = AppColor.Neutral10
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged {
                            with(density) {
                                imgSize.value = (it.height.toDp() - 8.dp)
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Profile Pic
                    when (targetUser.value) {
                        is Resource.Error -> {
                            Box(
                                modifier = Modifier
                                    .size(imgSize.value)
                                    .clip(CircleShape)
                                    .background(AppColor.Neutral100)
                            )
                        }
                        is Resource.Loading -> {
                            Box(
                                modifier = Modifier
                                    .size(imgSize.value)
                                    .clip(CircleShape)
                                    .placeholder(
                                        visible = true,
                                        color = AppColor.Neutral50,
                                        highlight = PlaceholderHighlight.shimmer(highlightColor = AppColor.Neutral20),
                                        shape = CircleShape
                                    )
                            )
                        }
                        is Resource.Success -> {
                            if((targetUser.value?.data?.profile_pic ?: "").isNotEmpty()){
                                AsyncImage(
                                    modifier = Modifier
                                        .size(imgSize.value)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    model = targetUser.value?.data?.profile_pic ?: "",
                                    contentDescription = "Img"
                                )
                            }else{
                                AsyncImage(
                                    modifier = Modifier
                                        .size(imgSize.value)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    model = R.drawable.ic_profile_pic,
                                    contentDescription = "Img"
                                )
                            }
                        }
                        null -> {}
                    }

                    // Target Name
                    when (targetUser.value) {
                        is Resource.Error -> {
                            AppText(text = "Error loading data", textType = TextType.H4)
                        }
                        is Resource.Loading -> {
                            AppText(text = ". . .", textType = TextType.H4)
                        }
                        is Resource.Success -> {
                            AppText(
                                text = targetUser.value?.data?.name ?: "", textType = TextType.H4
                            )
                        }
                        null -> {}
                    }
                }
            }
        },
        bottomBar = {
            Column {
                // Item if picked
                when {
                    product_id.isNotEmpty()
                            && viewModel.withItem.value.isNotEmpty() -> {
                        ChatProductItem(
                            product_id = viewModel.withItem.value,
                            viewModel = viewModel,
                            mainViewModel = mainViewModel,
                            navController = navController
                        )
                    }

                    permintaan_id.isNotEmpty()
                            && viewModel.withItem.value.isNotEmpty() -> {
                        ChatPermintaanItem(
                            permintaan_id = viewModel.withItem.value,
                            viewModel = viewModel,
                            mainViewModel = mainViewModel,
                            navController = navController
                        )
                    }
                }

                // Chat item
                Box(
                    modifier = Modifier
                        .heightIn(max = 250.dp)
                        .background(AppColor.Neutral20)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        AppTextInputField(
                            modifier = Modifier
                                .heightIn(max = 250.dp)
                                .weight(1f),
                            placeHolderText = "Chat here",
                            valueState = viewModel.chatInputState,
                            singleLine = false,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
                        )

                        AppButton(
                            onClick = {
                                val chatTmp = viewModel.chatInputState.value.trim()
                                viewModel.chatInputState.value = ""
                                if (chatTmp.isNotEmpty()) {
                                    if (viewModel.shouldCreateNew.value) {
                                        viewModel.createChannelId(
                                            user_1 = viewModel.getCurrentUid(),
                                            user_2 = target_uid,
                                            onSuccess = {
                                                viewModel.sendMessage(
                                                    channel_id = it,
                                                    chat = chatTmp,
                                                    sender = viewModel.getCurrentUid(),
                                                    receiver = target_uid,
                                                    product_id = if (product_id.isNotEmpty()) viewModel.withItem.value else "",
                                                    permintaan_id = if (permintaan_id.isNotEmpty()) viewModel.withItem.value else "",
                                                    onSuccess = {
                                                        viewModel.withItem.value = ""

                                                        // Send notification
                                                        viewModel.getTargetFcmToken(target_uid) { token ->
                                                            viewModel.sendNotification(
                                                                my_name = user.value?.data?.name
                                                                    ?: "",
                                                                my_message = chatTmp,
                                                                target_token = token
                                                            )
                                                        }

                                                        // Set firestore
                                                        viewModel.updateLastChatOnFirestore(
                                                            channel_id = viewModel.chatChannelId.value,
                                                            last_chat = chatTmp,
                                                            onSuccess = {
                                                                viewModel.withItem.value = ""
                                                                viewModel.shouldCreateNew.value =
                                                                    false
                                                            },
                                                            onFailed = {
                                                                /*TODO*/
                                                            }
                                                        )
                                                    },
                                                    onFailed = {
                                                        /*TODO*/
                                                    }
                                                )
                                            },
                                            onFailed = {
                                                /*TODO*/
                                            }
                                        )
                                    } else {
                                        viewModel.sendMessage(
                                            channel_id = viewModel.chatChannelId.value,
                                            chat = chatTmp,
                                            sender = viewModel.getCurrentUid(),
                                            receiver = target_uid,
                                            product_id = if (product_id.isNotEmpty()) viewModel.withItem.value else "",
                                            permintaan_id = if (permintaan_id.isNotEmpty()) viewModel.withItem.value else "",
                                            onSuccess = {
                                                viewModel.withItem.value = ""

                                                // Send notification
                                                viewModel.getTargetFcmToken(target_uid) { token ->
                                                    viewModel.sendNotification(
                                                        my_name = user.value?.data?.name ?: "",
                                                        my_message = chatTmp,
                                                        target_token = token
                                                    )
                                                }

                                                // Set firestore
                                                viewModel.updateLastChatOnFirestore(
                                                    channel_id = viewModel.chatChannelId.value,
                                                    last_chat = chatTmp,
                                                    onSuccess = {
                                                        viewModel.withItem.value = ""
                                                        viewModel.shouldCreateNew.value = false
                                                    },
                                                    onFailed = {
                                                        /*TODO*/
                                                    }
                                                )
                                            },
                                            onFailed = {
                                                /*TODO*/
                                            }
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = AppColor.Neutral10
                            )
                        }
                    }
                }
            }
        }
    ) {
        SwipeRefresh(state = swipeRefreshState, onRefresh = { /*TODO*/ }) {
            ChatDetailContent(
                navController = navController,
                viewModel = viewModel,
                targetUser = targetUser,
                paddingValues = it,
                mainViewModel = mainViewModel
            )
        }
    }
}

@Composable
private fun ChatDetailContent(
    navController: NavController,
    viewModel: ChatDetailViewModel,
    targetUser: State<Resource<UserModel>?>,
    paddingValues: PaddingValues,
    mainViewModel: MainViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding()),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            reverseLayout = true
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            viewModel.chatRoomSnapshot.value?.let {
                items(items = it.children.toList().asReversed()) { item ->
                    item?.let { chatRef ->
                        val chat = chatRef.getValue(ChatItemModel::class.java)

                        if (targetUser.value is Resource.Success) {
                            chat?.sender?.let { senderUid ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // If there is any product item
                                    when {
                                        (chat.product_id ?: "").isNotEmpty() -> {
                                            ChatProductItem(
                                                product_id = (chat.product_id ?: ""),
                                                viewModel = viewModel,
                                                mainViewModel = mainViewModel,
                                                navController = navController
                                            )
                                        }

                                        (chat.permintaan_id ?: "").isNotEmpty() -> {
                                            ChatPermintaanItem(
                                                permintaan_id = (chat.permintaan_id ?: ""),
                                                viewModel = viewModel,
                                                mainViewModel = mainViewModel,
                                                navController = navController
                                            )
                                        }
                                    }

                                    // Chat bubble
                                    when {
                                        (senderUid.equals(targetUser.value?.data?.uid ?: "")) -> {
                                            ChatBubbleOther(
                                                chat = chat.chat ?: "",
                                                pic_url = targetUser.value?.data?.profile_pic ?: "",
                                                product_id = chat.product_id ?: "",
                                                permintaan_id = chat.permintaan_id ?: ""
                                            )
                                        }

                                        else -> {
                                            ChatBubbleMe(
                                                chat = chat.chat ?: "",
                                                product_id = chat.product_id ?: "",
                                                permintaan_id = chat.permintaan_id ?: ""
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(0.dp))
            }
        }
    }
}

@Composable
private fun ChatBubbleMe(chat: String, product_id: String, permintaan_id: String) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Bubble
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        )
                    )
                    .background(AppColor.Neutral30), contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    text = chat,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun ChatBubbleOther(
    chat: String,
    pic_url: String,
    product_id: String,
    permintaan_id: String
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Profile Pic
            if(pic_url.isNotEmpty()){
                AsyncImage(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    model = pic_url,
                    contentDescription = "Profile Pic"
                )
            }else{
                AsyncImage(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    model = R.drawable.ic_profile_pic,
                    contentDescription = "Profile Pic"
                )
            }

            // Bubble
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topEnd = 16.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        )
                    )
                    .background(AppColor.Neutral30), contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    text = chat,
                    color = Color.Black
                )
            }
        }
    }
}