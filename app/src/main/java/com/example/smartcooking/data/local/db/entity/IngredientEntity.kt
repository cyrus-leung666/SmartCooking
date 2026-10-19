package com.example.smartcooking.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey @ColumnInfo(name = "ingredient_id") val ingredientId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "calories_per_100g") val caloriesPer100g: Double,
    @ColumnInfo(name = "protein_g") val proteinG: Double,
    @ColumnInfo(name = "carbs_g") val carbsG: Double,
    @ColumnInfo(name = "fat_g") val fatG: Double,
    @ColumnInfo(name = "category") val category: String
)
