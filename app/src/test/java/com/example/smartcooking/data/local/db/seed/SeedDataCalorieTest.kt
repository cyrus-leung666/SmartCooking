package com.example.smartcooking.data.local.db.seed

import com.example.smartcooking.domain.calculator.RecipeCalorieCalculator
import com.example.smartcooking.domain.model.RecipeIngredientItem
import org.junit.Assert.assertEquals
import org.junit.Test

class SeedDataCalorieTest {

    private val ingredients = SeedData.ingredients.associateBy { it.ingredientId }
    private val methods = SeedData.cookingMethods.associateBy { it.methodId }

    private fun caloriesOf(recipeId: String): Double {
        val items = SeedData.recipeIngredients
            .filter { it.recipeId == recipeId }
            .map { ri ->
                val ingredient = ingredients.getValue(ri.ingredientId)
                val method = methods.getValue(ri.methodId)
                RecipeIngredientItem(
                    name = ingredient.name,
                    category = ingredient.category,
                    weightG = ri.weightG,
                    methodName = method.methodName,
                    addedOilG = ri.addedOilG,
                    caloriesPer100g = ingredient.caloriesPer100g,
                    proteinG = ingredient.proteinG,
                    carbsG = ingredient.carbsG,
                    fatG = ingredient.fatG,
                    oilRetentionFactor = method.oilRetentionFactor
                )
            }
        return RecipeCalorieCalculator.calculate(items).totalCalories
    }

    @Test
    fun `new breakfast recipes have expected calories`() {
        assertEquals(303.3, caloriesOf("rec_milk_oatmeal"), 0.1)
        assertEquals(144.0, caloriesOf("rec_boiled_egg"), 0.1)
        assertEquals(147.6, caloriesOf("rec_whole_wheat_bread"), 0.1)
        assertEquals(152.5, caloriesOf("rec_milk"), 0.1)
        assertEquals(93.0, caloriesOf("rec_banana"), 0.1)
        assertEquals(246.0, caloriesOf("rec_tomato_egg_sandwich"), 0.1)
        assertEquals(154.5, caloriesOf("rec_yogurt_fruit_cup"), 0.1)
    }

    @Test
    fun `new lunch dinner staple has expected calories`() {
        assertEquals(232.0, caloriesOf("rec_steamed_rice"), 0.1)
    }

    @Test
    fun `meal type tags are present`() {
        assertEquals(
            "breakfast",
            SeedData.recipes.first { it.recipeId == "rec_milk_oatmeal" }.mealTypes
        )
        assertEquals(
            "lunch,dinner",
            SeedData.recipes.first { it.recipeId == "rec_steamed_rice" }.mealTypes
        )
        assertEquals(
            "lunch,dinner",
            SeedData.recipes.first { it.recipeId == "rec_scrambled_egg_tomato" }.mealTypes
        )
    }
}
