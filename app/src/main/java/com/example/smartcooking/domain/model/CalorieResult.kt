package com.example.smartcooking.domain.model

data class CalorieBreakdownItem(
    val name: String,
    val calories: Double
)

data class CalorieResult(
    val totalCalories: Double,
    val totalProteinG: Double,
    val totalCarbsG: Double,
    val totalFatG: Double,
    val breakdown: List<CalorieBreakdownItem>
)
