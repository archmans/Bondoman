package com.example.bondoman

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.bondoman.models.GraphData
import com.example.bondoman.retrofit.data.TransactionDB
import com.example.bondoman.retrofit.data.entity.Category
import com.example.bondoman.retrofit.data.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DBViewModel(application: Application) : AndroidViewModel(application) {
    private val db = TransactionDB.getInstance(application)
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    fun addTransaksi(name: String, category: String, price: Double, location: String = "") {
        viewModelScope.launch {
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val tempLocation = location
            val newTransaction = TransactionEntity(
                name = name,
                category = Category.valueOf(category),
                date = currentDate,
                price = price,
                location = tempLocation
            )
            db.transactionDao().insertAll(newTransaction)
        }
    }

    fun getGraphData(): List<GraphData> {
        val data: MutableList<GraphData> = mutableListOf()
        var dbData = listOf<GraphData>()
        viewModelScope.launch {
            dbData = db.transactionDao().sumPriceByCategory()
        }
        Log.d("VIEWMODELDATA", dbData.toString())
        if(dbData.size != 2) {
            if (dbData.size==1) {
                if (Category.valueOf(dbData[0].category!!) == Category.Pemasukan) {
                    data.add(0, dbData[0])
                    data.add(1, GraphData("Pengeluaran", 0.0))
                } else {
                    data.add(0, GraphData("Pemasukan", 0.0))
                    data.add(1, dbData[0])
                }
            } else {
                data.add(0, GraphData("Pemasukan", 0.0))
                data.add(1, GraphData("Pengeluaran", 0.0))
            }
        } else {
            data.add(0, dbData[0])
            data.add(1, dbData[1])
        }
        return data
    }
}