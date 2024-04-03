package com.example.bondoman.retrofit.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionCategory {
    PEMASUKAN,
    PENGELUARAN
}

@Entity
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "name") var name: String? = null,
    @ColumnInfo(name = "category") var category: TransactionCategory? = null,
    @ColumnInfo(name = "date") var date: String? = null,
    @ColumnInfo(name = "price") var price: Int? = null,
    @ColumnInfo(name = "location") var location: String? = null
)