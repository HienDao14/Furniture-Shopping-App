package com.example.furnitureshoppingapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressTitle: String,
    val fullName: String,
    val phoneNumber: String,
    val address: String,
    val city: String,
    val district: String,
    val ward: String,
    val additionalInfo: String
): Parcelable{
    constructor(): this("", "", "", "", "", "", "", "")
}