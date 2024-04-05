package com.example.bondoman

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
            var tempLocation = location
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

    fun getGraphData(): LiveData<List<GraphData>> {
        val data = MutableLiveData<List<GraphData>>()
        viewModelScope.launch {
            val dbData = db.transactionDao().sumPriceByCategory()
            data.postValue(dbData)
        }
        return data
    }

}