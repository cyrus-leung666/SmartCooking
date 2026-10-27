package com.example.smartcooking.domain.model

data class RecipeSummary(
    val recipeId: String,
    val title: String,
    val coverImageUrl: String,
    val cookingTimeMinutes: Int,
    val difficulty: String,
    val totalCalories: Double,
    val mealTypes: Set<MealType> = setOf(MealType.LUNCH, MealType.DINNER),
    val proteinG: Double = 0.0,
    val carbsG: Double = 0.0,
    val fatG: Double = 0.0
)
