package com.example.ttskotlin

import android.os.Parcel
import android.os.Parcelable

data class BookmarkedProduct(
    val name: String,
    val price: String,
    val rating: String,
    val sold: String,
    val image: String,
    val link: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(price)
        parcel.writeString(rating)
        parcel.writeString(sold)
        parcel.writeString(image)
        parcel.writeString(link)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookmarkedProduct> {
        override fun createFromParcel(parcel: Parcel): BookmarkedProduct {
            return BookmarkedProduct(parcel)
        }

        override fun newArray(size: Int): Array<BookmarkedProduct?> {
            return arrayOfNulls(size)
        }
    }
}
