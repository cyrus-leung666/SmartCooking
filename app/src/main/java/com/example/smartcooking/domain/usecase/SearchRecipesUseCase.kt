package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.repository.RecipeRepository
import javax.inject.Inject

class SearchRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(query: String, difficulty: String, maxTimeMinutes: Int): List<RecipeSummary> =
        recipeRepository.searchRecipes(query.trim(), difficulty, maxTimeMinutes)
}
