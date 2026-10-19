package com.example.smartcooking.data.local.db.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartcooking.data.local.db.entity.RecipeIngredientEntity

data class RecipeIngredientWithNames(
    @ColumnInfo(name = "recipe_id") val recipeId: String,
    @ColumnInfo(name = "ingredient_id") val ingredientId: String,
    @ColumnInfo(name = "method_id") val methodId: String,
    @ColumnInfo(name = "weight_g") val weightG: Double,
    @ColumnInfo(name = "added_oil_g") val addedOilG: Double,
    @ColumnInfo(name = "ingredient_name") val ingredientName: String,
    @ColumnInfo(name = "calories_per_100g") val caloriesPer100g: Double,
    @ColumnInfo(name = "ingredient_protein_g") val proteinG: Double,
    @ColumnInfo(name = "ingredient_carbs_g") val carbsG: Double,
    @ColumnInfo(name = "ingredient_fat_g") val fatG: Double,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "method_name") val methodName: String,
    @ColumnInfo(name = "oil_retention_factor") val oilRetentionFactor: Double
)

@Dao
interface RecipeIngredientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RecipeIngredientEntity>)

    @Query(
        """
        SELECT ri.recipe_id, ri.ingredient_id, ri.method_id, ri.weight_g, ri.added_oil_g,
               i.name AS ingredient_name, i.calories_per_100g,
               i.protein_g AS ingredient_protein_g, i.carbs_g AS ingredient_carbs_g,
               i.fat_g AS ingredient_fat_g, i.category,
               m.method_name, m.oil_retention_factor
        FROM recipe_ingredients ri
        JOIN ingredients i ON ri.ingredient_id = i.ingredient_id
        JOIN cooking_methods m ON ri.method_id = m.method_id
        WHERE ri.recipe_id = :recipeId
        """
    )
    suspend fun getItemsForRecipe(recipeId: String): List<RecipeIngredientWithNames>
}
