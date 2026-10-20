package com.example.smartcooking.domain.calculator

import com.example.smartcooking.domain.model.CalorieBreakdownItem
import com.example.smartcooking.domain.model.CalorieResult
import com.example.smartcooking.domain.model.RecipeIngredientItem
import kotlin.math.round

object RecipeCalorieCalculator {

    const val OIL_KCAL_PER_GRAM = 9.0
    const val OIL_BREAKDOWN_NAME = "Absorbed Cooking Oil"

    fun calculate(items: List<RecipeIngredientItem>): CalorieResult {
        var totalCalories = 0.0
        var totalProtein = 0.0
        var totalCarbs = 0.0
        var totalFat = 0.0
        var totalOilCalories = 0.0

        val breakdown = items.map { item ->
            val rawCalories = item.caloriesPer100g * item.weightG / 100.0
            val oilCalories = item.addedOilG * OIL_KCAL_PER_GRAM * item.oilRetentionFactor

            totalCalories += rawCalories + oilCalories
            totalProtein += item.proteinG * item.weightG / 100.0
            totalCarbs += item.carbsG * item.weightG / 100.0
            totalFat += item.fatG * item.weightG / 100.0 + item.addedOilG * item.oilRetentionFactor
            totalOilCalories += oilCalories

            CalorieBreakdownItem(name = item.name, calories = round1(rawCalories))
        }

        val allBreakdown = if (totalOilCalories > 0.0) {
            breakdown + CalorieBreakdownItem(name = OIL_BREAKDOWN_NAME, calories = round1(totalOilCalories))
        } else {
            breakdown
        }

        return CalorieResult(
            totalCalories = round1(totalCalories),
            totalProteinG = round1(totalProtein),
            totalCarbsG = round1(totalCarbs),
            totalFatG = round1(totalFat),
            breakdown = allBreakdown
        )
    }

    private fun round1(value: Double): Double = round(value * 10.0) / 10.0
}
