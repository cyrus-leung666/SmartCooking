package com.example.smartcooking.domain.mealplan

data class DailyMealPlan(
    val targetCalories: Double,
    val meals: List<PlannedMeal>
) {
    val totalCalories: Double get() = meals.sumOf { it.totalCalories }
    val proteinG: Double get() = meals.sumOf { meal -> meal.items.sumOf { it.proteinG } }
    val carbsG: Double get() = meals.sumOf { meal -> meal.items.sumOf { it.carbsG } }
    val fatG: Double get() = meals.sumOf { meal -> meal.items.sumOf { it.fatG } }
}
