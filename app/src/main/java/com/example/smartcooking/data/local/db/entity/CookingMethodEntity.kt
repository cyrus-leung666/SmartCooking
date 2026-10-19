package com.example.smartcooking.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cooking_methods")
data class CookingMethodEntity(
    @PrimaryKey @ColumnInfo(name = "method_id") val methodId: String,
    @ColumnInfo(name = "method_name") val methodName: String,
    @ColumnInfo(name = "oil_retention_factor") val oilRetentionFactor: Double
)
