package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.model.Ingredient
import com.example.smartcooking.domain.repository.RecipeRepository
import javax.inject.Inject

class GetIngredientsUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(): List<Ingredient> = recipeRepository.getIngredients()
}
