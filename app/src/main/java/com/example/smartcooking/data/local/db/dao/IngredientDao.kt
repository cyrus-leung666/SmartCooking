package com.example.smartcooking.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartcooking.data.local.db.entity.IngredientEntity

@Dao
interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<IngredientEntity>)

    @Query("SELECT * FROM ingredients ORDER BY category, name")
    suspend fun getAll(): List<IngredientEntity>
}
