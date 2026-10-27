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
import org.junit.Test

class GetRecommendedRecipesUseCaseTest {

    private val profile = UserProfile(Gender.MALE, 24, 175.0, 70.0, 1.375)

    @Test
    fun `filters out breakfast-only recipes before calling the assistant`() = runBlocking {
        val breakfast = RecipeSummary(
            "b1", "牛奶", "", 0, "简单", 152.5,
            mealTypes = setOf(MealType.BREAKFAST)
        )
        val lunch = RecipeSummary(
            "l1", "番茄炒蛋", "", 15, "简单", 309.0,
            mealTypes = setOf(MealType.LUNCH, MealType.DINNER)
        )
        val assistant = RecordingAssistant()
        val useCase = GetRecommendedRecipesUseCase(
            FakeUserRepository(profile),
            FakeRecipeRepository(listOf(breakfast, lunch)),
            assistant
        )

        useCase()

        assertEquals(listOf("l1"), assistant.received.map { it.recipeId })
    }

    private class RecordingAssistant : AiRecipeAssistant {
        var received: List<RecipeSummary> = emptyList()

        override suspend fun recommend(
            profile: UserProfile,
            recipes: List<RecipeSummary>,
            exclude: Set<String>
        ): List<RecipeSummary> {
            received = recipes
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
