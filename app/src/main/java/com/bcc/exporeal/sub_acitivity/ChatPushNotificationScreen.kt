package com.bcc.exporeal.sub_acitivity

import android.content.Intent
import android.os.Bundle
import com.bcc.exporeal.ExporealActivity
import com.bcc.exporeal.navController
import com.bcc.exporeal.navigation.AppNavRoute

class ChatPushNotificationScreen:ExporealActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extra = Intent(this, ChatPushNotificationScreen::class.java).getStringExtra("uid")

        navController.navigate(route = "${AppNavRoute.ChatDetailScreen.name}/${extra}")
    }
}