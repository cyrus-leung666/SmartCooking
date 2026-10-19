package com.example.smartcooking.data.local.db.seed

import com.example.smartcooking.data.local.db.dao.CookingMethodDao
import com.example.smartcooking.data.local.db.dao.IngredientDao
import com.example.smartcooking.data.local.db.dao.RecipeDao
import com.example.smartcooking.data.local.db.dao.RecipeIngredientDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val ingredientDao: IngredientDao,
    private val cookingMethodDao: CookingMethodDao,
    private val recipeDao: RecipeDao,
    private val recipeIngredientDao: RecipeIngredientDao
) {
    suspend fun seedIfNeeded() {
        if (ingredientDao.getAll().isNotEmpty()) return
        ingredientDao.insertAll(SeedData.ingredients)
        cookingMethodDao.insertAll(SeedData.cookingMethods)
        recipeDao.insertAll(SeedData.recipes)
        recipeIngredientDao.insertAll(SeedData.recipeIngredients)
    }
}
