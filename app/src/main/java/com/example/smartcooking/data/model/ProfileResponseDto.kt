package com.example.smartcooking.data.model

import com.google.gson.annotations.SerializedName

data class RecommendationDto(
    @SerializedName("target_calories") val targetCalories: Double,
    @SerializedName("protein_g") val proteinG: Double,
    @SerializedName("carbs_g") val carbsG: Double,
    @SerializedName("fat_g") val fatG: Double,
    @SerializedName("dietary_advice") val dietaryAdvice: String
)

data class ProfileResponseDto(
    val bmi: Double,
    @SerializedName("bmi_category") val bmiCategory: String,
    val bmr: Double,
    val tdee: Double,
    val recommendation: RecommendationDto
)
