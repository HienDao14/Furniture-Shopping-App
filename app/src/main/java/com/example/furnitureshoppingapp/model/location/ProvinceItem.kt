package com.example.furnitureshoppingapp.model.location

data class ProvinceItem(
    val code: Int,
    val codename: String,
    val districts: List<District>,
    val division_type: String,
    val name: String,
    val phone_code: Int
)