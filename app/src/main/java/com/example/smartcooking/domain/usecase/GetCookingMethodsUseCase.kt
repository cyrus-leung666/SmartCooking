package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.model.CookingMethod
import com.example.smartcooking.domain.repository.RecipeRepository
import javax.inject.Inject

class GetCookingMethodsUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(): List<CookingMethod> = recipeRepository.getCookingMethods()
}
