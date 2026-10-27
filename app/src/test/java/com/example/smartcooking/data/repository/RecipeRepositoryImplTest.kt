package com.example.smartcooking.data.repository

import com.example.smartcooking.data.local.db.dao.CookingMethodDao
import com.example.smartcooking.data.local.db.dao.IngredientDao
import com.example.smartcooking.data.local.db.dao.RecipeDao
import com.example.smartcooking.data.local.db.dao.RecipeIngredientDao
import com.example.smartcooking.data.local.db.dao.RecipeIngredientWithNames
import com.example.smartcooking.data.local.db.entity.CookingMethodEntity
import com.example.smartcooking.data.local.db.entity.IngredientEntity
import com.example.smartcooking.data.local.db.entity.RecipeEntity
import com.example.smartcooking.data.local.db.entity.RecipeIngredientEntity
import com.example.smartcooking.domain.model.MealType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeRepositoryImplTest {

    private val recipe = RecipeEntity(
        recipeId = "rec_test",
        title = "测试菜",
        description = "",
        coverImageUrl = "",
        cookingTimeMinutes = 10,
        difficulty = "简单",
        instructions = "[\"步骤\"]",
        createdAt = 0,
        mealTypes = "breakfast"
    )

    private val repository = RecipeRepositoryImpl(
        recipeDao = object : RecipeDao {
            override suspend fun insertAll(items: List<RecipeEntity>) = Unit
            override suspend fun getAll(): List<RecipeEntity> = listOf(recipe)
            override suspend fun getById(recipeId: String): RecipeEntity? = recipe
            override suspend fun searchRecipeIds(query: String, difficulty: String, maxTimeMinutes: Int): List<String> =
                listOf(recipe.recipeId)
        },
        recipeIngredientDao = object : RecipeIngredientDao {
            override suspend fun insertAll(items: List<RecipeIngredientEntity>) = Unit
            override suspend fun getItemsForRecipe(recipeId: String): List<RecipeIngredientWithNames> =
                listOf(
                    RecipeIngredientWithNames(
                        recipeId = recipe.recipeId,
                        ingredientId = "ing_test",
                        methodId = "mth_boil",
                        weightG = 100.0,
                        addedOilG = 0.0,
                        ingredientName = "测试食材",
                        caloriesPer100g = 100.0,
                        proteinG = 10.0,
                        carbsG = 20.0,
                        fatG = 5.0,
                        category = "主食",
                        methodName = "水煮",
                        oilRetentionFactor = 0.05
                    )
                )
        },
        ingredientDao = object : IngredientDao {
            override suspend fun insertAll(items: List<IngredientEntity>) = Unit
            override suspend fun getAll(): List<IngredientEntity> = emptyList()
        },
        cookingMethodDao = object : CookingMethodDao {
            override suspend fun insertAll(items: List<CookingMethodEntity>) = Unit
            override suspend fun getAll(): List<CookingMethodEntity> = emptyList()
        }
    )

    @Test
    fun `maps meal tags and macros from recipe data`() = runBlocking {
        val summary = repository.getRecipes().single()

        assertEquals(setOf(MealType.BREAKFAST), summary.mealTypes)
        assertEquals(100.0, summary.totalCalories, 0.01)
        assertEquals(10.0, summary.proteinG, 0.01)
        assertEquals(20.0, summary.carbsG, 0.01)
        assertEquals(5.0, summary.fatG, 0.01)
    }
}
