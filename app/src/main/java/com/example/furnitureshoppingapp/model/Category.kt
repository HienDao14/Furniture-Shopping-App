package com.example.furnitureshoppingapp.model

sealed class Category(val category: String) {
    object Chair: Category("chair")
    object Table: Category("table")
    object Cupboard: Category("cupboard")
    object Accessory: Category("accessory")
    object Furniture: Category("furniture")
}