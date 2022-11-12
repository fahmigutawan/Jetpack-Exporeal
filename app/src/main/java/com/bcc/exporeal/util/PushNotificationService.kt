package com.bcc.exporeal.util

import android.app.Service
import android.content.Intent
import android.os.IBinder

class PushNotificationService:Service() {
    override fun onCreate() {
        super.onCreate()


    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}