package com.example.smartcooking.domain.model

data class CalculatorItem(
    val ingredient: Ingredient,
    val weightG: Double,
    val method: CookingMethod,
    val addedOilG: Double
)
