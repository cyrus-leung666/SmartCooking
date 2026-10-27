package com.example.smartcooking.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey @ColumnInfo(name = "recipe_id") val recipeId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "cover_image_url") val coverImageUrl: String,
    @ColumnInfo(name = "cooking_time_minutes") val cookingTimeMinutes: Int,
    @ColumnInfo(name = "difficulty") val difficulty: String,
    @ColumnInfo(name = "instructions") val instructions: String, // JSON 字符串数组(规格书 §4 JSONB 的本地映射)
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "meal_types") val mealTypes: String = "lunch,dinner"
)
