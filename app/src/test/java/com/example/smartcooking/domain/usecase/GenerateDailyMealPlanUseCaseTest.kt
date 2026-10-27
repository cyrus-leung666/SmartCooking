package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.mealplan.DailyMealPlan
import com.example.smartcooking.domain.mealplan.MealPlanGenerator
import com.example.smartcooking.domain.mealplan.PlannedMeal
import com.example.smartcooking.domain.model.CookingMethod
import com.example.smartcooking.domain.model.Gender
import com.example.smartcooking.domain.model.Ingredient
import com.example.smartcooking.domain.model.MealType
import com.example.smartcooking.domain.model.RecipeDetail
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.repository.RecipeRepository
import com.example.smartcooking.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GenerateDailyMealPlanUseCaseTest {

    private val profile = UserProfile(Gender.MALE, 24, 175.0, 70.0, 1.375)
    private val plan = DailyMealPlan(
        targetCalories = 2308.28,
        meals = listOf(
            PlannedMeal(
                MealType.BREAKFAST,
                692.48,
                listOf(
                    RecipeSummary(
                        "b1", "牛奶", "", 0, "简单", 152.5,
                        setOf(MealType.BREAKFAST)
                    )
                )
            )
        )
    )

    @Test
    fun `returns null without calling the generator when profile is missing`() = runBlocking {
        val generator = RecordingGenerator(plan)

        val result = GenerateDailyMealPlanUseCase(
            FakeUserRepository(null),
            FakeRecipeRepository(emptyList()),
            generator
        )()

        assertNull(result)
        assertEquals(0, generator.calls)
    }

    @Test
    fun `passes exclude through and returns the generator result`() = runBlocking {
        val generator = RecordingGenerator(plan)

        val result = GenerateDailyMealPlanUseCase(
            FakeUserRepository(profile),
            FakeRecipeRepository(emptyList()),
            generator
        )(exclude = setOf("old_1", "old_2"))

        assertEquals(plan, result)
        assertEquals(setOf("old_1", "old_2"), generator.lastExclude)
        assertEquals(1, generator.calls)
    }

    private class RecordingGenerator(private val result: DailyMealPlan?) : MealPlanGenerator {
        var calls = 0
        var lastExclude: Set<String> = emptySet()

        override fun generate(
            profile: UserProfile,
            recipes: List<RecipeSummary>,
            exclude: Set<String>
        ): DailyMealPlan? {
            calls++
            lastExclude = exclude
            return result
        }
    }

    private class FakeUserRepository(private val profile: UserProfile?) : UserRepository {
        override fun getProfile(): Flow<UserProfile?> = flowOf(profile)
        override suspend fun saveProfile(profile: UserProfile) = Unit
    }

    private class FakeRecipeRepository(private val recipes: List<RecipeSummary>) : RecipeRepository {
        override suspend fun getRecipes(): List<RecipeSummary> = recipes
        override suspend fun searchRecipes(query: String, difficulty: String, maxTimeMinutes: Int): List<RecipeSummary> = recipes
        override suspend fun getRecipeDetail(recipeId: String): RecipeDetail? = null
        override suspend fun getIngredients(): List<Ingredient> = emptyList()
        override suspend fun getCookingMethods(): List<CookingMethod> = emptyList()
    }
}
