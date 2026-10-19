package com.example.smartcooking.domain.model

data class NutritionTarget(
    val carbsG: Double,
    val proteinG: Double,
    val fatG: Double,
    val ratioLabel: String,
    val dietaryAdvice: String
)

data class BmiResult(
    val bmi: Double,
    val category: String, // "Normal" / "Overweight" / "Underweight"
    val bmr: Double,
    val tdee: Double,
    val targetCalories: Double,
    val nutrition: NutritionTarget
)
