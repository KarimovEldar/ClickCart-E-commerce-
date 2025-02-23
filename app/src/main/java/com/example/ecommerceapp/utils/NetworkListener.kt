package com.example.ecommerceapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow

class NetworkListener : ConnectivityManager.NetworkCallback() {

    private val isNetworkAvailable = MutableStateFlow(false)

    fun checkNetworkAvailability(context: Context): MutableStateFlow<Boolean> {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(this)

        val network = connectivityManager.activeNetwork
        Log.d("Network", "active network $network")

        if(network == null){
            isNetworkAvailable.value = false
            return isNetworkAvailable
        }

        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        if(networkCapabilities == null){
            isNetworkAvailable.value = false
            return isNetworkAvailable
        }

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> { // check if wifi is connected
                Log.d("Network", "wifi connected")
                isNetworkAvailable.value = true
                isNetworkAvailable
            }

            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> { // check if mobile dats is connected
                Log.d("Network", "cellular network connected")
                isNetworkAvailable.value = true
                isNetworkAvailable
            }

            else -> {
                isNetworkAvailable.value = false
                isNetworkAvailable
            }
        }
    }

    override fun onAvailable(network: Network) {
        isNetworkAvailable.value = true
    }

    override fun onLost(network: Network) {
        isNetworkAvailable.value = false
    }

}