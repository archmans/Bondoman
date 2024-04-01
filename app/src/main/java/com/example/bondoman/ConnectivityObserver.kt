package com.example.bondoman

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe() : Flow<NetworkState>

    enum class NetworkState {
        CONNECTED, DISCONNECTED
    }
}