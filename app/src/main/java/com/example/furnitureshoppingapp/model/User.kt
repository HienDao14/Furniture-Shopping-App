package com.example.furnitureshoppingapp.model

data class User(
    val userName: String,
    val email: String,
    var imgPath: String = ""
){
    constructor(): this( "", "", "")
}
