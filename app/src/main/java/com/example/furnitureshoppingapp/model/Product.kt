package com.example.furnitureshoppingapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product (
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<String>? = null,
    val sizes: List<String>? = null,
    val images: List<String>
): Parcelable {
    constructor(): this("0", "", "", 0f, images = emptyList())
}

//      055947
//        E4CBAD
//        B4916C
//      A9907E
//      F3DEBA
//      ABC4AA
//          808080
//          DBDBDB
//      7D4C28
//      325452