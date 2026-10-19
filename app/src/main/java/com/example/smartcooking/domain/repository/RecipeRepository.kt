package com.example.smartcooking.domain.repository

import com.example.smartcooking.domain.model.CookingMethod
import com.example.smartcooking.domain.model.Ingredient
import com.example.smartcooking.domain.model.RecipeDetail
import com.example.smartcooking.domain.model.RecipeSummary

interface RecipeRepository {
    suspend fun getRecipes(): List<RecipeSummary>
    suspend fun searchRecipes(query: String, difficulty: String, maxTimeMinutes: Int): List<RecipeSummary>
    suspend fun getRecipeDetail(recipeId: String): RecipeDetail?
    suspend fun getIngredients(): List<Ingredient>
    suspend fun getCookingMethods(): List<CookingMethod>
}
