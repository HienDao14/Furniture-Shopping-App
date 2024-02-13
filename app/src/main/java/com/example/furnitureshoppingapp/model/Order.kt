package com.example.furnitureshoppingapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order (
    val orderStatus : String,
    val totalPrice : Float,
    val products: List<CartProduct>,
    val address: Address
): Parcelable{
    constructor(): this("Ordered", 0f, ArrayList(), Address())
}