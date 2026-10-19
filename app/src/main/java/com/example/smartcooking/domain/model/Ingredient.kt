package com.example.smartcooking.domain.model

data class Ingredient(
    val ingredientId: String,
    val name: String,
    val caloriesPer100g: Double,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
    val category: String
)
