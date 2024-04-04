package com.example.bondoman.utils

import com.example.bondoman.appInterface.api.ApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = Utils.BASE_URL

    val api: ApiInterface by lazy {
        createRetrofit().create(ApiInterface::class.java)
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
