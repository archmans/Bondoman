package com.example.bondoman.sql

import android.provider.BaseColumns

internal class Kontraktor {
    internal class TransactionTable : BaseColumns {
        companion object {
            const val TABLE_NAME = "transactions"
            const val _ID = "_id"
            const val NAME = "name"
            const val CATEGORY = "category"
            const val DATE = "date"
            const val PRICE = "price"
            const val LOCATION = "location"
        }
    }
}