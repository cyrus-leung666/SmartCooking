package com.example.smartcooking.data.mealplan

import com.example.smartcooking.domain.model.Gender
import com.example.smartcooking.domain.model.MealType
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.usecase.CalculateBmiUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.random.Random

class LocalRuleBasedMealPlanGeneratorTest {

    private val profile = UserProfile(Gender.MALE, 24, 175.0, 70.0, 1.375)
    private val target = CalculateBmiUseCase()(Gender.MALE, 24, 175.0, 70.0, 1.375).targetCalories

    private val recipes = listOf(
        recipe("b_oat", "牛奶燕麦粥", 303.3, setOf(MealType.BREAKFAST)),
        recipe("b_egg", "水煮蛋", 144.0, setOf(MealType.BREAKFAST)),
        recipe("b_bread", "全麦面包", 147.6, setOf(MealType.BREAKFAST)),
        recipe("b_milk", "牛奶", 152.5, setOf(MealType.BREAKFAST)),
        recipe("b_banana", "香蕉", 93.0, setOf(MealType.BREAKFAST)),
        recipe("b_sandwich", "番茄鸡蛋三明治", 246.0, setOf(MealType.BREAKFAST)),
        recipe("b_yogurt", "酸奶水果杯", 154.5, setOf(MealType.BREAKFAST)),
        recipe("l_tomato", "番茄炒蛋", 309.0, lunchDinner()),
        recipe("l_chicken", "清蒸鸡胸肉配西兰花", 381.0, lunchDinner()),
        recipe("l_salmon", "香煎三文鱼配土豆", 439.4, lunchDinner()),
        recipe("l_beef", "红烧牛肉炖土豆", 589.8, lunchDinner()),
        recipe("l_cucumber", "凉拌黄瓜", 44.5, lunchDinner()),
        recipe("l_noodles", "西红柿鸡蛋面", 569.9, lunchDinner()),
        recipe("l_rice", "米饭", 232.0, lunchDinner())
    )

    private fun generator(seed: Int = 42) =
        LocalRuleBasedMealPlanGenerator(CalculateBmiUseCase(), Random(seed))

    @Test
    fun `returns three meals with correct types and budgets`() {
        val plan = requireNotNull(generator().generate(profile, recipes))

        assertEquals(
            listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER),
            plan.meals.map { it.type }
        )
        assertEquals(target * 0.30, plan.meals[0].targetCalories, 0.01)
        assertEquals(target * 0.40, plan.meals[1].targetCalories, 0.01)
        assertEquals(target * 0.30, plan.meals[2].targetCalories, 0.01)
        assertTrue(plan.meals.all { it.items.isNotEmpty() })
    }

    @Test
    fun `breakfast contains only breakfast recipes`() {
        repeat(10) { seed ->
            val plan = requireNotNull(generator(seed).generate(profile, recipes))
            assertTrue(plan.meals[0].items.all { MealType.BREAKFAST in it.mealTypes })
        }
    }

    @Test
    fun `lunch and dinner contain only lunch or dinner recipes`() {
        repeat(10) { seed ->
            val plan = requireNotNull(generator(seed).generate(profile, recipes))
            assertTrue(plan.meals[1].items.all { MealType.LUNCH in it.mealTypes })
            assertTrue(plan.meals[2].items.all { MealType.DINNER in it.mealTypes })
        }
    }

    @Test
    fun `plan is within tolerance and has no cross meal duplicates`() {
        repeat(10) { seed ->
            val plan = requireNotNull(generator(seed).generate(profile, recipes))
            val ids = plan.meals.flatMap { meal -> meal.items.map { it.recipeId } }

            assertEquals(ids.size, ids.toSet().size)
            assertTrue(abs(plan.totalCalories - plan.targetCalories) / plan.targetCalories <= 0.10)
            plan.meals.forEach { meal ->
                assertTrue(abs(meal.totalCalories - meal.targetCalories) / meal.targetCalories <= 0.25)
            }
        }
    }

    @Test
    fun `exclude produces a different plan when alternatives exist`() {
        val first = requireNotNull(generator(42).generate(profile, recipes))
        val exclude = first.meals.flatMap { it.items }.map { it.recipeId }.toSet()
        val second = requireNotNull(generator(42).generate(profile, recipes, exclude))

        assertTrue(
            first.meals.flatMap { it.items }.map { it.recipeId }.toSet() !=
                second.meals.flatMap { it.items }.map { it.recipeId }.toSet()
        )
    }

    @Test
    fun `same seed produces same plan`() {
        assertEquals(
            generator(7).generate(profile, recipes),
            generator(7).generate(profile, recipes)
        )
    }

    @Test
    fun `returns null for non-positive target`() {
        val extreme = UserProfile(Gender.MALE, 2124, 175.0, 20.0, 1.2)
        assertNull(generator().generate(extreme, recipes))
    }

    @Test
    fun `returns null when a meal pool is empty`() {
        val lunchDinnerOnly = recipes.filter { MealType.BREAKFAST !in it.mealTypes }
        assertNull(generator().generate(profile, lunchDinnerOnly))
    }

    private fun recipe(
        id: String,
        title: String,
        calories: Double,
        mealTypes: Set<MealType>
    ) = RecipeSummary(id, title, "", 10, "简单", calories, mealTypes)

    private fun lunchDinner() = setOf(MealType.LUNCH, MealType.DINNER)
}
