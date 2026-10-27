package com.example.smartcooking.data.local.db

import com.example.smartcooking.domain.model.MealType

object MealTypesCodec {

    private val default = setOf(MealType.LUNCH, MealType.DINNER)

    fun decode(raw: String): Set<MealType> {
        val decoded = raw.split(',')
            .mapNotNull { token ->
                when (token.trim().lowercase()) {
                    "breakfast" -> MealType.BREAKFAST
                    "lunch" -> MealType.LUNCH
                    "dinner" -> MealType.DINNER
                    else -> null
                }
            }
            .toSet()
        return if (decoded.isEmpty()) default else decoded
    }

    fun encode(types: Set<MealType>): String =
        types.sortedBy { it.ordinal }.joinToString(",") { it.name.lowercase() }
}
