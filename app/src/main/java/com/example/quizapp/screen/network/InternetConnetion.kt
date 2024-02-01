package com.example.quizapp.screen.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED
import androidx.core.content.getSystemService


object InternetConnection {
    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(currentNetwork)
        return capabilities?.hasCapability(NET_CAPABILITY_INTERNET) == true && capabilities.hasCapability(
            NET_CAPABILITY_VALIDATED
        )
    }



}