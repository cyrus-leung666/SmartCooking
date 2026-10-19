package com.example.smartcooking.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartcooking.data.local.db.entity.CookingMethodEntity

@Dao
interface CookingMethodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CookingMethodEntity>)

    @Query("SELECT * FROM cooking_methods")
    suspend fun getAll(): List<CookingMethodEntity>
}
