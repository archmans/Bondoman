package com.example.bondoman.api

import com.example.bondoman.models.LoginRequest
import com.example.bondoman.models.LoginResponse
import com.example.bondoman.models.JWTResponse
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Header

interface ApiInterface {
    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("/api/auth/token")
    suspend fun jwt(@Header("Authorization") token: String): JWTResponse
}