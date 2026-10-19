package com.example.smartcooking.domain.calculator

import com.example.smartcooking.domain.model.RecipeIngredientItem
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeCalorieCalculatorTest {

    private fun item(
        name: String,
        weightG: Double,
        addedOilG: Double,
        alpha: Double,
        kcal: Double,
        protein: Double = 0.0,
        carbs: Double = 0.0,
        fat: Double = 0.0
    ) = RecipeIngredientItem(
        name = name,
        category = "测试",
        weightG = weightG,
        methodName = "炒",
        addedOilG = addedOilG,
        caloriesPer100g = kcal,
        proteinG = protein,
        carbsG = carbs,
        fatG = fat,
        oilRetentionFactor = alpha
    )

    @Test
    fun `tomato scrambled egg with stir fry oil matches expected totals`() {
        val result = RecipeCalorieCalculator.calculate(
            listOf(
                item("鸡蛋", 150.0, 10.0, 0.70, kcal = 144.0, protein = 13.3, carbs = 2.8, fat = 8.8),
                item("番茄", 200.0, 0.0, 0.70, kcal = 15.0, protein = 0.9, carbs = 3.3, fat = 0.2)
            )
        )
        assertEquals(309.0, result.totalCalories, 0.05)
        assertEquals(21.8, result.totalProteinG, 0.05)
        assertEquals(10.8, result.totalCarbsG, 0.05)
        assertEquals(20.6, result.totalFatG, 0.05)
        assertEquals(3, result.breakdown.size)
        assertEquals("鸡蛋", result.breakdown[0].name)
        assertEquals(216.0, result.breakdown[0].calories, 0.05)
        assertEquals("烹饪吸收油脂", result.breakdown[2].name)
        assertEquals(63.0, result.breakdown[2].calories, 0.05)
    }

    @Test
    fun `steamed chicken with no oil produces no oil breakdown entry`() {
        val result = RecipeCalorieCalculator.calculate(
            listOf(
                item("鸡胸肉(去皮)", 200.0, 0.0, 0.05, kcal = 165.0, protein = 31.0),
                item("西兰花", 150.0, 0.0, 0.05, kcal = 34.0, protein = 4.1)
            )
        )
        assertEquals(381.0, result.totalCalories, 0.05)
        assertEquals(2, result.breakdown.size)
    }

    @Test
    fun `deep frying absorbs far more oil calories than boiling`() {
        val fried = RecipeCalorieCalculator.calculate(
            listOf(item("土豆", 100.0, 10.0, 1.35, kcal = 77.0))
        )
        val boiled = RecipeCalorieCalculator.calculate(
            listOf(item("土豆", 100.0, 10.0, 0.05, kcal = 77.0))
        )
        assertEquals(198.5, fried.totalCalories, 0.05) // 77 + 10*9*1.35 = 198.5
        assertEquals(81.5, boiled.totalCalories, 0.05) // 77 + 10*9*0.05 = 81.5
    }

    @Test
    fun `empty items yields zero result`() {
        val result = RecipeCalorieCalculator.calculate(emptyList())
        assertEquals(0.0, result.totalCalories, 0.001)
        assertEquals(0, result.breakdown.size)
    }

    @Test
    fun `oil from multiple ingredients aggregates into one breakdown entry`() {
        val result = RecipeCalorieCalculator.calculate(
            listOf(
                item("鸡蛋", 100.0, 10.0, 0.70, kcal = 144.0, protein = 13.3, carbs = 2.8, fat = 8.8),
                item("番茄", 200.0, 10.0, 0.70, kcal = 15.0, protein = 0.9, carbs = 3.3, fat = 0.2)
            )
        )
        // 食材 2 项 + 聚合油 1 项 = 3 项;油 = (10+10)*9*0.70 = 126.0
        assertEquals(3, result.breakdown.size)
        assertEquals("烹饪吸收油脂", result.breakdown[2].name)
        assertEquals(126.0, result.breakdown[2].calories, 0.05)
        // 番茄为 breakdown[1],值 30.0(顺带钉住顺序契约)
        assertEquals("番茄", result.breakdown[1].name)
        assertEquals(30.0, result.breakdown[1].calories, 0.05)
        // 总热量 = 144 + 30 + 126 = 300.0
        assertEquals(300.0, result.totalCalories, 0.05)
    }
}
