package com.example.bondoman

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.bondoman.retrofit.data.TransactionDB
import com.example.bondoman.retrofit.data.entity.Category
import com.example.bondoman.retrofit.data.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DBViewModel(application: Application) : AndroidViewModel(application) {
    private val db = TransactionDB.getInstance(application)

    fun addTransaksi(name: String, category: String, price: Int, location: String = "") {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val newTransaction = TransactionEntity(
            name = name,
            category = Category.valueOf(category),
            date = currentDate,
            price = price,
            location = location
        )
        db.transactionDao().insertAll(newTransaction)
    }
}