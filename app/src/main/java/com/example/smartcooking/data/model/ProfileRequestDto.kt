package com.example.smartcooking.data.model

import com.google.gson.annotations.SerializedName

data class ProfileRequestDto(
    val gender: String,
    val age: Int,
    @SerializedName("height_cm") val heightCm: Double,
    @SerializedName("weight_kg") val weightKg: Double,
    @SerializedName("activity_level") val activityLevel: Double
)
