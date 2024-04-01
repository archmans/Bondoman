package com.example.bondoman

import android.content.Context
import android.net.ConnectivityManager
import kotlinx.coroutines.flow.Flow
import com.example.bondoman.ConnectivityObserver.NetworkState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import android.net.Network

class NetworkSensing (private val context: Context) : ConnectivityObserver {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    override fun observe(): Flow<NetworkState> {
        return callbackFlow {
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    trySend(NetworkState.CONNECTED)
                }

                override fun onLost(network: Network) {
                    trySend(NetworkState.DISCONNECTED)
                }
            }
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
            awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
        }.distinctUntilChanged()
    }
}