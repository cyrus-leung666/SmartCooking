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
        // 钉住映射的其余 6 个字段(proteinG/carbsG/fatG 经计算体现,name/category/methodName 经 breakdown 名称体现):
        // 鸡蛋 150g:蛋白 13.3*1.5=19.95、碳水 2.8*1.5=4.2、脂肪 8.8*1.5+10*0.7=20.2
        // 番茄 200g:蛋白 1.8、碳水 6.6、脂肪 0.4
        // 蛋白合计 19.95+1.8=21.75,经 round1 半偶舍入为 21.8(与 RecipeCalorieCalculatorTest 钉死的值一致)
        assertEquals(21.8, result.totalProteinG, 0.05)
        assertEquals(10.8, result.totalCarbsG, 0.05)
        assertEquals(20.6, result.totalFatG, 0.05)
        assertEquals(3, result.breakdown.size)
        assertEquals("鸡蛋", result.breakdown[0].name)
        assertEquals("番茄", result.breakdown[1].name)
        assertEquals("烹饪吸收油脂", result.breakdown.last().name)
        assertEquals(63.0, result.breakdown.last().calories, 0.05)
    }
}
