package com.example.smartcooking.data.repository

import com.example.smartcooking.data.local.db.dao.RecipeDao
import com.example.smartcooking.data.local.db.dao.RecipeIngredientDao
import com.example.smartcooking.data.local.db.dao.RecipeIngredientWithNames
import com.example.smartcooking.data.local.db.dao.IngredientDao
import com.example.smartcooking.data.local.db.dao.CookingMethodDao
import com.example.smartcooking.data.local.db.entity.RecipeEntity
import com.example.smartcooking.domain.calculator.RecipeCalorieCalculator
import com.example.smartcooking.domain.model.CookingMethod
import com.example.smartcooking.domain.model.Ingredient
import com.example.smartcooking.domain.model.RecipeDetail
import com.example.smartcooking.domain.model.RecipeIngredientItem
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.repository.RecipeRepository
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

// 数据主源为 Room 本地库;Retrofit 留桩启用后,替换本类实现即可切换为远程数据源
@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val recipeIngredientDao: RecipeIngredientDao,
    private val ingredientDao: IngredientDao,
    private val cookingMethodDao: CookingMethodDao
) : RecipeRepository {

    override suspend fun getRecipes(): List<RecipeSummary> =
        recipeDao.getAll().map { it.toSummary() }

    override suspend fun searchRecipes(query: String, difficulty: String, maxTimeMinutes: Int): List<RecipeSummary> {
        val ids = recipeDao.searchRecipeIds(query, difficulty, maxTimeMinutes)
        return ids.mapNotNull { recipeId -> recipeDao.getById(recipeId)?.toSummary() }
    }

    override suspend fun getRecipeDetail(recipeId: String): RecipeDetail? {
        val entity = recipeDao.getById(recipeId) ?: return null
        return RecipeDetail(
            recipeId = entity.recipeId,
            title = entity.title,
            description = entity.description,
            coverImageUrl = entity.coverImageUrl,
            cookingTimeMinutes = entity.cookingTimeMinutes,
            difficulty = entity.difficulty,
            instructions = parseInstructions(entity.instructions),
            items = recipeIngredientDao.getItemsForRecipe(recipeId).map { it.toRecipeIngredientItem() }
        )
    }

    override suspend fun getIngredients(): List<Ingredient> =
        ingredientDao.getAll().map {
            Ingredient(
                ingredientId = it.ingredientId,
                name = it.name,
                caloriesPer100g = it.caloriesPer100g,
                proteinG = it.proteinG,
                carbsG = it.carbsG,
                fatG = it.fatG,
                category = it.category
            )
        }

    override suspend fun getCookingMethods(): List<CookingMethod> =
        cookingMethodDao.getAll().map {
            CookingMethod(
                methodId = it.methodId,
                methodName = it.methodName,
                oilRetentionFactor = it.oilRetentionFactor
            )
        }

    private suspend fun RecipeEntity.toSummary(): RecipeSummary {
        val items = recipeIngredientDao.getItemsForRecipe(recipeId).map { it.toRecipeIngredientItem() }
        return RecipeSummary(
            recipeId = recipeId,
            title = title,
            coverImageUrl = coverImageUrl,
            cookingTimeMinutes = cookingTimeMinutes,
            difficulty = difficulty,
            totalCalories = RecipeCalorieCalculator.calculate(items).totalCalories
        )
    }

    private fun RecipeIngredientWithNames.toRecipeIngredientItem() = RecipeIngredientItem(
        name = ingredientName,
        category = category,
        weightG = weightG,
        methodName = methodName,
        addedOilG = addedOilG,
        caloriesPer100g = caloriesPer100g,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = fatG,
        oilRetentionFactor = oilRetentionFactor
    )

    private fun parseInstructions(json: String): List<String> = try {
        val array = JSONArray(json)
        (0 until array.length()).map { array.getString(it) }
    } catch (e: Exception) {
        emptyList()
    }
}
