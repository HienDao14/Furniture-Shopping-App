package com.example.furnitureshoppingapp.model.location

data class District(
    val code: Int,
    val codename: String,
    val division_type: String,
    val name: String,
    val province_code: Int,
    val wards: List<Ward>
)