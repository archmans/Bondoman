package com.example.bondoman.services

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe() : Flow<NetworkState>

    enum class NetworkState {
        CONNECTED, DISCONNECTED
    }
}