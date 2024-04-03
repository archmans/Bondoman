package com.example.bondoman.retrofit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bondoman.retrofit.data.dao.TransactionDao
import com.example.bondoman.retrofit.data.entity.TransactionEntity

@Database(entities = [TransactionEntity::class], version = 1)
abstract class TransactionDB : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object{
        private var instance : TransactionDB? = null

        fun getInstance(context: Context) : TransactionDB{
            if (instance==null){
                instance = Room.databaseBuilder(context, TransactionDB::class.java, "transaction-database")
                    .allowMainThreadQueries()
                    .build()
            }

            return instance!!
        }
    }
}