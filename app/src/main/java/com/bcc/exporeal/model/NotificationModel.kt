package com.bcc.exporeal.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationModel(
    val to:String,
    val data:Notification
)

@Serializable
data class Notification(
    val body:String,
    val title:String,
    val tag:String
)
