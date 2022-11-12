package com.bcc.exporeal.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bcc.exporeal.ExporealActivity
import com.bcc.exporeal.R
import com.bcc.exporeal.repository.AppRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject

class AppFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var repository: AppRepository

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "Chat Notification"

        var builder = NotificationCompat.Builder(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificatioChannel = NotificationChannel(
                channelId,
                "Chat",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificatioChannel)

            builder = NotificationCompat.Builder(this, channelId)
        }

        val intent = Intent(this, ExporealActivity::class.java)
        intent.putExtra("uid", message.data.get("tag"))
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.apply {
            setContentTitle("Message from ${message.data.get("title")}")
            setContentText(message.data.get("body"))
            setSmallIcon(R.drawable.ic_logo_only)
            setContentIntent(pendingIntent)
            setFullScreenIntent(pendingIntent, true)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setCategory(NotificationCompat.CATEGORY_ALARM)
        }

        notificationManager.notify(100, builder.build())

//        val receiverIntent = Intent(this, PushNotificationReceiver::class.java)
//        val receiverPendingIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, 0)
//
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        alarmManager.setExact(
//            AlarmManager.RTC_WAKEUP,
//            0,
//            receiverPendingIntent
//        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        repository.saveFcmTokenToFirestore(token)
    }
}