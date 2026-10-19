package com.example.smartcooking.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartcooking.data.local.db.entity.RecipeEntity

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RecipeEntity>)

    @Query("SELECT * FROM recipes")
    suspend fun getAll(): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE recipe_id = :recipeId")
    suspend fun getById(recipeId: String): RecipeEntity?

    // 设计文档 §8.1:多表 JOIN + LIKE 模糊搜索,按 recipeId 去重
    // 哨兵值约定:query="" 不限关键词、difficulty="" 不限难度、maxTimeMinutes=0 不限时长
    // (注意必须加 =0 守卫,否则 "<= 0" 会过滤掉全部菜谱)
    @Query(
        """
        SELECT DISTINCT r.recipe_id FROM recipes r
        LEFT JOIN recipe_ingredients ri ON r.recipe_id = ri.recipe_id
        LEFT JOIN ingredients i ON ri.ingredient_id = i.ingredient_id
        WHERE (:query = '' OR r.title LIKE '%' || :query || '%' OR i.name LIKE '%' || :query || '%')
          AND (:difficulty = '' OR r.difficulty = :difficulty)
          AND (:maxTimeMinutes = 0 OR r.cooking_time_minutes <= :maxTimeMinutes)
        """
    )
    suspend fun searchRecipeIds(query: String, difficulty: String, maxTimeMinutes: Int): List<String>
}
