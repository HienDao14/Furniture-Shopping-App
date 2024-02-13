package com.example.furnitureshoppingapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor: String?= null
): Parcelable{
    constructor(): this(Product(), 1)
}