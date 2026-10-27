package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.ai.AiRecipeAssistant
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GetRecommendedRecipesUseCaseTest {

    private val profile = UserProfile(Gender.MALE, 24, 175.0, 70.0, 1.375)

    private fun recipe(
        recipeId: String,
        title: String,
        calories: Double,
        mealTypes: Set<MealType>
    ) = RecipeSummary(
        recipeId = recipeId,
        title = title,
        coverImageUrl = "",
        cookingTimeMinutes = 10,
        difficulty = "简单",
        totalCalories = calories,
        mealTypes = mealTypes
    )

    @Test
    fun `filters out breakfast-only recipes before calling the assistant`() = runBlocking {
        val breakfast = recipe("b1", "牛奶", 152.5, setOf(MealType.BREAKFAST))
        val lunch = recipe("l1", "番茄炒蛋", 309.0, setOf(MealType.LUNCH, MealType.DINNER))
        val assistant = RecordingAssistant()
        val useCase = GetRecommendedRecipesUseCase(
            FakeUserRepository(profile),
            FakeRecipeRepository(listOf(breakfast, lunch)),
            assistant
        )

        useCase()

        assertEquals(listOf("l1"), assistant.received.map { it.recipeId })
    }

    @Test
    fun `keeps lunch-only and dinner-only recipes and drops breakfast-only`() = runBlocking {
        val breakfast = recipe("b1", "牛奶", 152.5, setOf(MealType.BREAKFAST))
        val lunchAndDinner = recipe("l1", "番茄炒蛋", 309.0, setOf(MealType.LUNCH, MealType.DINNER))
        val lunchOnly = recipe("l2", "鸡胸肉沙拉", 320.0, setOf(MealType.LUNCH))
        val dinnerOnly = recipe("d1", "清蒸鱼", 350.0, setOf(MealType.DINNER))
        val assistant = RecordingAssistant()
        val useCase = GetRecommendedRecipesUseCase(
            FakeUserRepository(profile),
            FakeRecipeRepository(listOf(breakfast, lunchAndDinner, lunchOnly, dinnerOnly)),
            assistant
        )

        useCase()

        assertEquals(listOf("l1", "l2", "d1"), assistant.received.map { it.recipeId })
    }

    @Test
    fun `passes exclude through to the assistant`() = runBlocking {
        val assistant = RecordingAssistant()
        val useCase = GetRecommendedRecipesUseCase(
            FakeUserRepository(profile),
            FakeRecipeRepository(listOf(recipe("l1", "番茄炒蛋", 309.0, setOf(MealType.LUNCH)))),
            assistant
        )

        useCase(exclude = setOf("l1"))

        assertEquals(setOf("l1"), assistant.receivedExclude)
    }

    @Test
    fun `returns empty without calling the assistant when profile is missing`() = runBlocking {
        val assistant = RecordingAssistant()
        val useCase = GetRecommendedRecipesUseCase(
            FakeUserRepository(null),
            FakeRecipeRepository(listOf(recipe("l1", "番茄炒蛋", 309.0, setOf(MealType.LUNCH)))),
            assistant
        )

        assertTrue(useCase().isEmpty())
        assertFalse(assistant.called)
    }

    private class RecordingAssistant : AiRecipeAssistant {
        var received: List<RecipeSummary> = emptyList()
        var receivedExclude: Set<String> = emptySet()
        var called: Boolean = false

        override suspend fun recommend(
            profile: UserProfile,
            recipes: List<RecipeSummary>,
            exclude: Set<String>
        ): List<RecipeSummary> {
            called = true
            received = recipes
            receivedExclude = exclude
            return recipes.take(2)
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
