package com.example.native202411pub

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

class MyNetworkStatus private constructor(context: Context) {

    companion object {
        @Volatile
        private var Instance: MyNetworkStatus? = null

        fun getNetworkStatus(context: Context): MyNetworkStatus {
            return Instance ?: synchronized(this) {
                MyNetworkStatus(context).also { Instance = it }
            }
        }
    }

    private val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()
    private val manager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val isConnectFow = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            val list = mutableListOf<Network>()
            override fun onAvailable(network: Network) {
                logger.trace("onAvailable {}", network)
                list.add(network)
                trySend(true)
            }

            override fun onUnavailable() {
                logger.trace("onUnavailable")
                trySend(false)
            }

            override fun onLost(network: Network) {
                logger.trace("onLost {}", network)
                list.remove(network)
                if (list.isEmpty()) {
                    trySend(false)
                }
            }
        }
        manager.registerNetworkCallback(request, callback)
        awaitClose {
            manager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged().flowOn(Dispatchers.IO)
}
