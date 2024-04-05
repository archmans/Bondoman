package com.example.bondoman.models

import android.os.Parcel
import android.os.Parcelable

data class SqlTransaction(var id: Int=0, var name: String? = null, var category: String? =null, var date: String? =null, var price: Double? = null, var location : String? =null) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Double,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(category)
        parcel.writeString(date)
        parcel.writeValue(price)
        parcel.writeString(location)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SqlTransaction> {
        override fun createFromParcel(parcel: Parcel): SqlTransaction {
            return SqlTransaction(parcel)
        }

        override fun newArray(size: Int): Array<SqlTransaction?> {
            return arrayOfNulls(size)
        }
    }
}