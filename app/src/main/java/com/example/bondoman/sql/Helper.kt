package com.example.bondoman.sql

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Helper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "bondoman"
        private const val DATABASE_VERSION = 1

        private const val SQL_CREATE = "CREATE TABLE ${Kontraktor.TransactionTable.TABLE_NAME} " +
                "(${Kontraktor.TransactionTable._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${Kontraktor.TransactionTable.NAME} TEXT NOT NULL, " +
                "${Kontraktor.TransactionTable.CATEGORY} TEXT NOT NULL, " +
                "${Kontraktor.TransactionTable.DATE} TEXT NOT NULL, " +
                "${Kontraktor.TransactionTable.PRICE} INTEGER NOT NULL, " +
                "${Kontraktor.TransactionTable.LOCATION} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${Kontraktor.TransactionTable.TABLE_NAME}")
        onCreate(db)
    }
}
