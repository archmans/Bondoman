package com.example.bondoman.retrofit.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.bondoman.models.GraphData
import com.example.bondoman.retrofit.data.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactionentity")
    fun getAll(): List<TransactionEntity>

    @Query("SELECT * FROM transactionentity WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<TransactionEntity>

    @Query("SELECT * FROM transactionentity WHERE nama_transaksi LIKE :name AND " +
            "nominal_transaksi LIKE :nominal LIMIT 1")
    fun findByName(name: String, nominal: String): TransactionEntity

    @Insert
    fun insertAll(vararg transactions: TransactionEntity)

    @Delete
    fun delete(transaction: TransactionEntity)

    @Update
    fun update(transaction: TransactionEntity)
    @Query("SELECT * FROM transactionentity WHERE id = :id")
    fun getId(id: Int): TransactionEntity
    @Query("SELECT kategori_transaksi as category, SUM(nominal_transaksi) as amount FROM transactionentity GROUP BY kategori_transaksi")
    fun sumPriceByCategory(): List<GraphData>
}
