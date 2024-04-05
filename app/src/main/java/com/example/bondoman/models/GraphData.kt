package com.example.bondoman.models

import androidx.room.ColumnInfo
import com.example.bondoman.retrofit.data.entity.Category

data class GraphData(
    @ColumnInfo(name = "category") val category: String?,
    @ColumnInfo(name = "amount") val amount: Double?
)
