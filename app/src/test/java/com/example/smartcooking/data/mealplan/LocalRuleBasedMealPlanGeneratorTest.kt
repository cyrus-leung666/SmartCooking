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
        val firstIds = first.meals.flatMap { it.items }.map { it.recipeId }.toSet()
        val second = requireNotNull(generator(42).generate(profile, recipes, firstIds))
        val secondIds = second.meals.flatMap { it.items }.map { it.recipeId }.toSet()

        assertTrue(firstIds != secondIds)
        assertEquals(1, firstIds.intersect(secondIds).size)
    }

    @Test
    fun `same seed produces same plan`() {
        val first = requireNotNull(generator(7).generate(profile, recipes))
        val second = requireNotNull(generator(7).generate(profile, recipes))
        assertEquals(first, second)
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

    @Test
    fun `every meal contains one to three items`() {
        val plan = requireNotNull(generator().generate(profile, recipes))
        assertTrue(plan.meals.all { it.items.size in 1..3 })
    }

    @Test
    fun `returns null for empty recipes`() {
        assertNull(generator().generate(profile, emptyList()))
    }

    @Test
    fun `returns null when duplicate-only pools make a plan impossible`() {
        val only = recipe(
            "only", "唯一菜", 500.0,
            setOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER)
        )
        assertNull(generator().generate(profile, listOf(only)))
    }

    @Test
    fun `uses relaxed tolerance when no strict plan exists`() {
        val relaxedProfile = UserProfile(Gender.MALE, 30, 190.0, 100.0, 2.0)
        val plan = requireNotNull(generator(42).generate(relaxedProfile, recipes))

        assertTrue(plan.meals.any { meal ->
            abs(meal.totalCalories - meal.targetCalories) / meal.targetCalories > 0.25
        })
        plan.meals.forEach { meal ->
            assertTrue(abs(meal.totalCalories - meal.targetCalories) / meal.targetCalories <= 0.35)
        }
        assertTrue(abs(plan.totalCalories - plan.targetCalories) / plan.targetCalories <= 0.15)
    }

    @Test
    fun `returns best-effort fallback when no relaxed plan exists`() {
        val fallbackProfile = UserProfile(Gender.MALE, 20, 195.0, 90.0, 2.0)
        val plan = requireNotNull(generator(42).generate(fallbackProfile, recipes))

        assertTrue(plan.meals.any { meal ->
            abs(meal.totalCalories - meal.targetCalories) / meal.targetCalories > 0.35
        })
    }

    @Test
    fun `single-tag recipes only appear in their own meal`() {
        val lunchOnly = recipe("l_only", "午餐专属", 400.0, setOf(MealType.LUNCH))
        val dinnerOnly = recipe("d_only", "晚餐专属", 400.0, setOf(MealType.DINNER))
        val plan = requireNotNull(generator(42).generate(profile, recipes + lunchOnly + dinnerOnly))

        assertTrue(plan.meals[1].items.none { it.recipeId == "d_only" })
        assertTrue(plan.meals[2].items.none { it.recipeId == "l_only" })
    }

    @Test
    fun `expanded limits find a plan when top forty combos all share one recipe`() {
        val shared = listOf(
            recipe("main10", "共享主菜", 600.0, lunchDinner())
        ) + (1..9).map { i ->
            recipe("side10_$i", "共享配菜$i", 50.0 + i * 10.0, lunchDinner())
        }
        val breakfastOnly = recipes.filter { MealType.BREAKFAST in it.mealTypes }

        val plan = requireNotNull(generator(42).generate(profile, breakfastOnly + shared))

        assertTrue(plan.meals.all { it.items.isNotEmpty() })
        val ids = plan.meals.flatMap { meal -> meal.items.map { it.recipeId } }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `diversity rescue finds a plan when top one hundred sixty combos all share one recipe`() {
        val shared = listOf(
            recipe("main", "共享主菜", 600.0, lunchDinner())
        ) + (1..18).map { i ->
            recipe("side_$i", "共享配菜$i", 50.0 + i * 10.0, lunchDinner())
        }
        val breakfastOnly = recipes.filter { MealType.BREAKFAST in it.mealTypes }
        val relaxedProfile = UserProfile(Gender.MALE, 30, 190.0, 100.0, 2.0)

        val plan = requireNotNull(generator(42).generate(relaxedProfile, breakfastOnly + shared))

        assertTrue(plan.meals.all { it.items.isNotEmpty() })
        val ids = plan.meals.flatMap { meal -> meal.items.map { it.recipeId } }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `prefers a strict plan found at a larger limit over an early relaxed plan`() {
        val strictProfile = UserProfile(Gender.MALE, 24, 175.0, 70.0, 1.2)
        val breakfastPool = listOf(
            recipe("b470", "小份早餐", 470.0, setOf(MealType.BREAKFAST))
        ) + (1..10).map { i ->
            recipe("b300_$i", "早餐$i", 300.0, setOf(MealType.BREAKFAST))
        }
        val lunchPool = listOf(
            recipe("l950", "午餐主菜", 950.0, setOf(MealType.LUNCH))
        ) + (1..10).map { i ->
            recipe("l100_$i", "午餐配$i", 100.0, setOf(MealType.LUNCH))
        }
        val dinnerPool = listOf(
            recipe("d750", "晚餐主菜", 750.0, setOf(MealType.DINNER))
        ) + (1..10).map { i ->
            recipe("d100_$i", "晚餐配$i", 100.0, setOf(MealType.DINNER))
        }

        val plan = requireNotNull(
            generator(42).generate(strictProfile, breakfastPool + lunchPool + dinnerPool)
        )

        assertTrue(plan.meals[0].items.any { it.recipeId == "b470" })
        assertTrue(abs(plan.totalCalories - plan.targetCalories) / plan.targetCalories <= 0.10)
    }

    @Test
    fun `diversity rescue respects the pool cap for very large pools`() {
        val shared = listOf(
            recipe("big_main", "大池主菜", 600.0, lunchDinner())
        ) + (1..61).map { i ->
            recipe("big_side_$i", "大池配菜$i", 50.0 + i * 2.0, lunchDinner())
        }
        val breakfastOnly = recipes.filter { MealType.BREAKFAST in it.mealTypes }
        val relaxedProfile = UserProfile(Gender.MALE, 30, 190.0, 100.0, 2.0)

        val plan = requireNotNull(generator(42).generate(relaxedProfile, breakfastOnly + shared))
        val ids = plan.meals.flatMap { meal -> meal.items.map { it.recipeId } }

        assertEquals(ids.size, ids.toSet().size)
        assertTrue(plan.meals.all { it.items.isNotEmpty() })
    }

    private fun recipe(
        id: String,
        title: String,
        calories: Double,
        mealTypes: Set<MealType>
    ) = RecipeSummary(id, title, "", 10, "简单", calories, mealTypes)

    private fun lunchDinner() = setOf(MealType.LUNCH, MealType.DINNER)
}
