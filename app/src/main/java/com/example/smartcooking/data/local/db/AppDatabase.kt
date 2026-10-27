package com.example.smartcooking.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.smartcooking.data.local.db.dao.CookingMethodDao
import com.example.smartcooking.data.local.db.dao.IngredientDao
import com.example.smartcooking.data.local.db.dao.RecipeDao
import com.example.smartcooking.data.local.db.dao.RecipeIngredientDao
import com.example.smartcooking.data.local.db.dao.UserDao
import com.example.smartcooking.data.local.db.entity.CookingMethodEntity
import com.example.smartcooking.data.local.db.entity.IngredientEntity
import com.example.smartcooking.data.local.db.entity.RecipeEntity
import com.example.smartcooking.data.local.db.entity.RecipeIngredientEntity
import com.example.smartcooking.data.local.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        IngredientEntity::class,
        CookingMethodEntity::class,
        RecipeEntity::class,
        RecipeIngredientEntity::class
    ],
    version = 3, // v3: 新增 meal_types 列与早餐/主食种子;破坏性迁移后重种
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun cookingMethodDao(): CookingMethodDao
    abstract fun recipeDao(): RecipeDao
    abstract fun recipeIngredientDao(): RecipeIngredientDao
}
