package com.example.bondoman.models

import java.io.File

data class ScanRequest (
    val token: String,
    val file: File,
    )