package com.example.bondoman.retrofit.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Category {
    Pemasukan,
    Pengeluaran
}

@Entity
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "nama_transaksi") var name: String? = null,
    @ColumnInfo(name = "kategori_transaksi") var category: Category? = null,
    @ColumnInfo(name = "tanggal_transaksi") var date: String? = null,
    @ColumnInfo(name = "nominal_transaksi") var price: Int? = null,
    @ColumnInfo(name = "lokasi_transaksi") var location: String? = null
)