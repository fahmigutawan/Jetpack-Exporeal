package com.bcc.exporeal.util

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

class ConnectivityCheck @Inject constructor(private val context: Context){
    fun isConnectedToInternet():Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo?.isConnected ?: false
    }
}
