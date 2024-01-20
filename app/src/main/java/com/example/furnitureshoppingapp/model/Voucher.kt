package com.example.furnitureshoppingapp.model

data class Voucher(
    val text: String,
    val discount : Int,
    val unit: String
){
    constructor(): this("", 0, "")
}