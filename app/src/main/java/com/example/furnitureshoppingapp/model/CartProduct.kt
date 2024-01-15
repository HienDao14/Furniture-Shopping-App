package com.example.furnitureshoppingapp.model

data class CartProduct(
    val product: Product,
    val quantity: Int,
    val selectedColor: String?= null
){
    constructor(): this(Product(), 1)
}