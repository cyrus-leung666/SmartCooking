package com.example.smartcooking.data.model

import com.google.gson.annotations.SerializedName

data class CalorieBreakdownDto(
    val name: String,
    val calories: Double
)

data class CalorieResponseDto(
    @SerializedName("total_calories") val totalCalories: Double,
    @SerializedName("total_protein_g") val totalProteinG: Double,
    @SerializedName("total_carbs_g") val totalCarbsG: Double,
    @SerializedName("total_fat_g") val totalFatG: Double,
    val breakdown: List<CalorieBreakdownDto>
)
