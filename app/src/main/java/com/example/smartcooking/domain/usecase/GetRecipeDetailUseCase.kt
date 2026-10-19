package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.calculator.RecipeCalorieCalculator
import com.example.smartcooking.domain.model.CalorieResult
import com.example.smartcooking.domain.model.RecipeDetail
import com.example.smartcooking.domain.repository.RecipeRepository
import javax.inject.Inject

data class RecipeDetailWithCalories(
    val detail: RecipeDetail,
    val calories: CalorieResult
)

class GetRecipeDetailUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(recipeId: String): RecipeDetailWithCalories? {
        val detail = recipeRepository.getRecipeDetail(recipeId) ?: return null
        return RecipeDetailWithCalories(detail, RecipeCalorieCalculator.calculate(detail.items))
    }
}
