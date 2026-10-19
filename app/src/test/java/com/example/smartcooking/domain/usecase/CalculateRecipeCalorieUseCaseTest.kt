package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.model.CalculatorItem
import com.example.smartcooking.domain.model.CookingMethod
import com.example.smartcooking.domain.model.Ingredient
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateRecipeCalorieUseCaseTest {

    private val useCase = CalculateRecipeCalorieUseCase()

    @Test
    fun `maps calculator items to calorie result`() {
        val egg = Ingredient("ing_egg", "鸡蛋", 144.0, 13.3, 2.8, 8.8, "蛋奶类")
        val tomato = Ingredient("ing_tomato", "番茄", 15.0, 0.9, 3.3, 0.2, "蔬菜")
        val stirFry = CookingMethod("mth_stir_fry", "炒", 0.70)

        val result = useCase(
            listOf(
                CalculatorItem(egg, 150.0, stirFry, 10.0),
                CalculatorItem(tomato, 200.0, stirFry, 0.0)
            )
        )

        assertEquals(309.0, result.totalCalories, 0.05)
        assertEquals(3, result.breakdown.size)
        assertEquals("烹饪吸收油脂", result.breakdown.last().name)
        assertEquals(63.0, result.breakdown.last().calories, 0.05)
    }
}
