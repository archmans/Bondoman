package com.example.bondoman.appInterface.api

import com.example.bondoman.models.LoginRequest
import com.example.bondoman.models.LoginResponse
import com.example.bondoman.models.JWTResponse
import com.example.bondoman.models.ScanResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ApiInterface {
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("/api/auth/token")
    suspend fun jwt(@Header("Authorization") token: String): Response<JWTResponse>

    @Multipart
    @POST("api/bill/upload")
    suspend fun uploadBill(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<ScanResponse>
}