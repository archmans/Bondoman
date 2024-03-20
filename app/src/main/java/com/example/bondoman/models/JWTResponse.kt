package com.example.bondoman.models

data class JWTResponse(
    val nim: String,
    val iat: Int,
    val exp: Int
)
