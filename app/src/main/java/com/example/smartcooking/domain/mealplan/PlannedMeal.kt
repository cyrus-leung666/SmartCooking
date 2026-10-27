package com.example.smartcooking.domain.mealplan

import com.example.smartcooking.domain.model.MealType
import com.example.smartcooking.domain.model.RecipeSummary

data class PlannedMeal(
    val type: MealType,
    val targetCalories: Double,
    val items: List<RecipeSummary>
) {
    val totalCalories: Double get() = items.sumOf { it.totalCalories }
}
