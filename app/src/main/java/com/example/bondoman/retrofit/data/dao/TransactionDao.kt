package com.example.bondoman.retrofit.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.bondoman.retrofit.data.entity.TransactionEntity

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactionentity")
    fun getAll(): List<TransactionEntity>

    @Query("SELECT * FROM transactionentity WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<TransactionEntity>

    @Query("SELECT * FROM transactionentity WHERE name LIKE :name AND " +
            "price LIKE :nominal LIMIT 1")
    fun findByName(name: String, nominal: String): TransactionEntity

    @Insert
    fun insertAll(vararg transactions: TransactionEntity)

    @Delete
    fun delete(transaction: TransactionEntity)
}