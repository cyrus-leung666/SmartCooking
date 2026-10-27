package com.example.smartcooking.domain.mealplan

import com.example.smartcooking.domain.model.MealType
import com.example.smartcooking.domain.model.RecipeSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyMealPlanTest {

    @Test
    fun `sums calories and macros across meals`() {
        val breakfast = RecipeSummary(
            recipeId = "b1",
            title = "早餐",
            coverImageUrl = "",
            cookingTimeMinutes = 0,
            difficulty = "简单",
            totalCalories = 100.0,
            mealTypes = setOf(MealType.BREAKFAST),
            proteinG = 10.0,
            carbsG = 20.0,
            fatG = 5.0
        )
        val lunch = RecipeSummary(
            recipeId = "l1",
            title = "午餐",
            coverImageUrl = "",
            cookingTimeMinutes = 0,
            difficulty = "简单",
            totalCalories = 200.0,
            mealTypes = setOf(MealType.LUNCH),
            proteinG = 20.0,
            carbsG = 30.0,
            fatG = 10.0
        )
        val plan = DailyMealPlan(
            targetCalories = 300.0,
            meals = listOf(
                PlannedMeal(MealType.BREAKFAST, 100.0, listOf(breakfast)),
                PlannedMeal(MealType.LUNCH, 200.0, listOf(lunch))
            )
        )

        assertEquals(300.0, plan.totalCalories, 0.01)
        assertEquals(30.0, plan.proteinG, 0.01)
        assertEquals(50.0, plan.carbsG, 0.01)
        assertEquals(15.0, plan.fatG, 0.01)
    }
}
