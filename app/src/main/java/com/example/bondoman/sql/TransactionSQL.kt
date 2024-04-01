package com.example.bondoman.sql

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.bondoman.models.SqlTransaction
import com.example.bondoman.sql.Kontraktor.TransactionTable.Companion.CATEGORY
import com.example.bondoman.sql.Kontraktor.TransactionTable.Companion.DATE
import com.example.bondoman.sql.Kontraktor.TransactionTable.Companion.LOCATION
import com.example.bondoman.sql.Kontraktor.TransactionTable.Companion.NAME
import com.example.bondoman.sql.Kontraktor.TransactionTable.Companion.PRICE
import com.example.bondoman.sql.Kontraktor.TransactionTable.Companion.TABLE_NAME
import com.example.bondoman.sql.Kontraktor.TransactionTable.Companion._ID
import java.sql.SQLException

class TransactionSQL(context: Context) {
    private var dbHelper: Helper = Helper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME

        private val INSTANCE: TransactionSQL? = null
        fun getInstance(context: Context) : TransactionSQL = INSTANCE ?: synchronized(this) {
            INSTANCE ?: TransactionSQL(context)
        }
    }

    @Throws(SQLException::class)
    fun open() {
        database = dbHelper.writableDatabase
    }

    fun close() {
        dbHelper.close()
        if (database.isOpen) {
            database.close()
        }
    }

    fun findAll():List<SqlTransaction> {
        val cursor = database.query(
            DATABASE_TABLE,
            null,null,null,null,null,"$_ID ASC"
        )

        val listData = mutableListOf<SqlTransaction>()

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(_ID))
                val name = getString(getColumnIndexOrThrow(NAME))
                val category = getString(getColumnIndexOrThrow(CATEGORY))
                val date = getString(getColumnIndexOrThrow(DATE))
                val price = getInt(getColumnIndexOrThrow(PRICE))
                val location = getString(getColumnIndexOrThrow(LOCATION))

                listData.add(SqlTransaction(id,name,category,date,price,location))
            }
            cursor.close()
        }
        return listData
    }

    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun update(id: String, values: ContentValues?): Int {
        return database.update(DATABASE_TABLE, values, "$_ID = ?", arrayOf(id))
    }

    fun delete(id: String): Int {
        return database.delete(DATABASE_TABLE, "$_ID = '$id'", null)
    }

    fun deleteAll() {
        database.delete(DATABASE_TABLE, null, null)
    }

}
