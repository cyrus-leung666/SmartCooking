package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.mealplan.DailyMealPlan
import com.example.smartcooking.domain.mealplan.MealPlanGenerator
import com.example.smartcooking.domain.repository.RecipeRepository
import com.example.smartcooking.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenerateDailyMealPlanUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val mealPlanGenerator: MealPlanGenerator
) {
    suspend operator fun invoke(exclude: Set<String> = emptySet()): DailyMealPlan? {
        val profile = userRepository.getProfile().first() ?: return null
        val recipes = recipeRepository.getRecipes()
        return withContext(Dispatchers.Default) {
            mealPlanGenerator.generate(profile, recipes, exclude)
        }
    }
}
