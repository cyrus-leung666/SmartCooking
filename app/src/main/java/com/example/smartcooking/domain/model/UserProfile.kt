package com.example.smartcooking.domain.model

data class UserProfile(
    val gender: Gender,
    val age: Int,
    val heightCm: Double,
    val weightKg: Double,
    val activityLevel: Double
)
