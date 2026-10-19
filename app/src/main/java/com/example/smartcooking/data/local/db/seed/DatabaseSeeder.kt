package com.example.smartcooking.data.local.db.seed

import androidx.room.withTransaction
import com.example.smartcooking.data.local.db.AppDatabase
import com.example.smartcooking.data.local.db.dao.CookingMethodDao
import com.example.smartcooking.data.local.db.dao.IngredientDao
import com.example.smartcooking.data.local.db.dao.RecipeDao
import com.example.smartcooking.data.local.db.dao.RecipeIngredientDao
import com.example.smartcooking.domain.seed.SeedStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val database: AppDatabase,
    private val ingredientDao: IngredientDao,
    private val cookingMethodDao: CookingMethodDao,
    private val recipeDao: RecipeDao,
    private val recipeIngredientDao: RecipeIngredientDao
) : SeedStatus {

    private val _seedCompleted = MutableStateFlow(false)
    override val seedCompleted: StateFlow<Boolean> = _seedCompleted.asStateFlow()

    suspend fun seedIfNeeded() {
        // 守卫检查最后写入的表:四步插入在同一事务内,recipes 存在即种子完整
        if (recipeDao.getAll().isNotEmpty()) {
            _seedCompleted.value = true
            return
        }
        // 单事务保证原子性:中断不会留下部分种子;REPLACE 保证重跑幂等
        database.withTransaction {
            ingredientDao.insertAll(SeedData.ingredients)
            cookingMethodDao.insertAll(SeedData.cookingMethods)
            recipeDao.insertAll(SeedData.recipes)
            recipeIngredientDao.insertAll(SeedData.recipeIngredients)
        }
        _seedCompleted.value = true
    }
}
