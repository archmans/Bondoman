package com.example.bondoman.api

import com.example.bondoman.models.LoginRequest
import com.example.bondoman.models.LoginResponse
import retrofit2.http.POST
import retrofit2.http.Body

interface ApiInterface {
    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}