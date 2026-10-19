package com.example.smartcooking.domain.model

data class RecipeSummary(
    val recipeId: String,
    val title: String,
    val coverImageUrl: String,
    val cookingTimeMinutes: Int,
    val difficulty: String,
    val totalCalories: Double
)
