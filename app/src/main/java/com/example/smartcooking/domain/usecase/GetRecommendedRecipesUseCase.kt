package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.ai.AiRecipeAssistant
import com.example.smartcooking.domain.model.MealType
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.repository.RecipeRepository
import com.example.smartcooking.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetRecommendedRecipesUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val aiRecipeAssistant: AiRecipeAssistant
) {
    suspend operator fun invoke(exclude: Set<String> = emptySet()): List<RecipeSummary> {
        val profile = userRepository.getProfile().first() ?: return emptyList()
        val recipes = recipeRepository.getRecipes()
            .filter { MealType.LUNCH in it.mealTypes || MealType.DINNER in it.mealTypes }
        return aiRecipeAssistant.recommend(profile, recipes, exclude)
    }
}
