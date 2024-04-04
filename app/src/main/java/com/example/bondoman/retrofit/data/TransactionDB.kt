package com.example.bondoman.retrofit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bondoman.retrofit.data.dao.TransactionDao
import com.example.bondoman.retrofit.data.entity.TransactionEntity

<<<<<<< app/src/main/java/com/example/bondoman/retrofit/data/TransactionDB.kt
@Database(entities = [TransactionEntity::class], version = 2)
=======
@Database(entities = [TransactionEntity::class], version = 1)
>>>>>>> app/src/main/java/com/example/bondoman/retrofit/data/TransactionDB.kt
abstract class TransactionDB : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object{
        private var instance : TransactionDB? = null

        fun getInstance(context: Context) : TransactionDB{
            if (instance==null){
                instance = Room.databaseBuilder(context, TransactionDB::class.java, "db")
                    .allowMainThreadQueries()
                    .build()
            }

            return instance!!
        }
    }
}