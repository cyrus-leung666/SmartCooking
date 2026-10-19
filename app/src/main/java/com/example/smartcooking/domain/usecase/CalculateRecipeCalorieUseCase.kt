package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.calculator.RecipeCalorieCalculator
import com.example.smartcooking.domain.model.CalorieResult
import com.example.smartcooking.domain.model.CalculatorItem
import com.example.smartcooking.domain.model.RecipeIngredientItem
import javax.inject.Inject

class CalculateRecipeCalorieUseCase @Inject constructor() {

    operator fun invoke(items: List<CalculatorItem>): CalorieResult {
        val mapped = items.map { item ->
            RecipeIngredientItem(
                name = item.ingredient.name,
                category = item.ingredient.category,
                weightG = item.weightG,
                methodName = item.method.methodName,
                addedOilG = item.addedOilG,
                caloriesPer100g = item.ingredient.caloriesPer100g,
                proteinG = item.ingredient.proteinG,
                carbsG = item.ingredient.carbsG,
                fatG = item.ingredient.fatG,
                oilRetentionFactor = item.method.oilRetentionFactor
            )
        }
        return RecipeCalorieCalculator.calculate(mapped)
    }
}
