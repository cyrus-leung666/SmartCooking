package com.example.smartcooking.domain.model

data class RecipeIngredientItem(
    val name: String,
    val category: String,
    val weightG: Double,
    val methodName: String,
    val addedOilG: Double,
    val caloriesPer100g: Double,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
    val oilRetentionFactor: Double
)

data class RecipeDetail(
    val recipeId: String,
    val title: String,
    val description: String,
    val coverImageUrl: String,
    val cookingTimeMinutes: Int,
    val difficulty: String,
    val instructions: List<String>,
    val items: List<RecipeIngredientItem>
)
