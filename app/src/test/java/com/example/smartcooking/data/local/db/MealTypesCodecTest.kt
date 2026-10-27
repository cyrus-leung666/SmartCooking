package com.example.smartcooking.data.local.db

import com.example.smartcooking.domain.model.MealType
import org.junit.Assert.assertEquals
import org.junit.Test

class MealTypesCodecTest {

    @Test
    fun `decodes breakfast tag`() {
        assertEquals(setOf(MealType.BREAKFAST), MealTypesCodec.decode("breakfast"))
    }

    @Test
    fun `decodes multiple tags and ignores unknown values`() {
        assertEquals(
            setOf(MealType.LUNCH, MealType.DINNER),
            MealTypesCodec.decode("lunch, dinner, snack")
        )
    }

    @Test
    fun `falls back to lunch and dinner for empty or unknown input`() {
        assertEquals(setOf(MealType.LUNCH, MealType.DINNER), MealTypesCodec.decode(""))
        assertEquals(setOf(MealType.LUNCH, MealType.DINNER), MealTypesCodec.decode("snack"))
    }

    @Test
    fun `encode round trips`() {
        val types = setOf(MealType.BREAKFAST, MealType.DINNER)
        assertEquals(types, MealTypesCodec.decode(MealTypesCodec.encode(types)))
    }
}
