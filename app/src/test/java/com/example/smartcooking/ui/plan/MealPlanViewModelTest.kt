package com.example.smartcooking.ui.plan

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
import com.example.smartcooking.domain.seed.SeedStatus
import com.example.smartcooking.domain.usecase.GenerateDailyMealPlanUseCase
import com.example.smartcooking.domain.usecase.GetUserProfileUseCase
import com.example.smartcooking.domain.usecase.ObserveSeedCompletionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MealPlanViewModelTest {

    private val dispatcher = StandardTestDispatcher()
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

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `missing profile sets profileMissing and does not generate`() = runTest(dispatcher) {
        val generator = FakeGenerator(plan)

        val viewModel = viewModel(null, seedCompleted = true, generator = generator)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.profileMissing)
        assertFalse(viewModel.uiState.value.loading)
        assertNull(viewModel.uiState.value.plan)
        assertEquals(0, generator.calls)
    }

    @Test
    fun `incomplete seed keeps loading without showing an error`() = runTest(dispatcher) {
        val generator = FakeGenerator(null)

        val viewModel = viewModel(profile, seedCompleted = false, generator = generator)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.loading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `null plan after seed completes sets an error and clears the plan`() = runTest(dispatcher) {
        val generator = FakeGenerator(null)

        val viewModel = viewModel(profile, seedCompleted = true, generator = generator)
        // WithContext(Dispatchers.Default) escapes the test scheduler; await the settled state.
        viewModel.uiState.first { !it.loading }

        assertFalse(viewModel.uiState.value.loading)
        assertNotNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.plan)
    }

    @Test
    fun `successful generation stores the plan`() = runTest(dispatcher) {
        val generator = FakeGenerator(plan)

        val viewModel = viewModel(profile, seedCompleted = true, generator = generator)
        viewModel.uiState.first { it.plan != null }

        assertEquals(plan, viewModel.uiState.value.plan)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `profile change triggers regeneration`() = runTest(dispatcher) {
        val userRepository = FakeUserRepository(profile)
        val generator = FakeGenerator(plan)
        val viewModel = viewModel(
            profile,
            seedCompleted = true,
            generator = generator,
            userRepository = userRepository
        )
        viewModel.uiState.first { it.plan != null }
        val callsAfterInit = generator.calls

        userRepository.profile.value = profile.copy(weightKg = 60.0)
        generator.callCount.first { it > callsAfterInit }

        assertTrue(generator.calls > callsAfterInit)
    }

    @Test
    fun `refresh clears a previous error and stores a new plan`() = runTest(dispatcher) {
        val generator = FakeGenerator(null)
        val viewModel = viewModel(profile, seedCompleted = true, generator = generator)
        viewModel.uiState.first { it.error != null }
        assertNotNull(viewModel.uiState.value.error)

        generator.result = plan
        viewModel.onRefresh()
        viewModel.uiState.first { it.plan != null }

        assertNull(viewModel.uiState.value.error)
        assertEquals(plan, viewModel.uiState.value.plan)
    }

    private fun viewModel(
        profile: UserProfile?,
        seedCompleted: Boolean,
        generator: FakeGenerator,
        userRepository: FakeUserRepository = FakeUserRepository(profile)
    ): MealPlanViewModel {
        val useCase = GenerateDailyMealPlanUseCase(
            userRepository,
            FakeRecipeRepository(),
            generator
        )
        return MealPlanViewModel(
            useCase,
            ObserveSeedCompletionUseCase(FakeSeedStatus(seedCompleted)),
            GetUserProfileUseCase(userRepository)
        )
    }

    private class FakeGenerator(var result: DailyMealPlan?) : MealPlanGenerator {
        var calls = 0
        var lastExclude: Set<String> = emptySet()
        private val _callCount = MutableStateFlow(0)
        val callCount: StateFlow<Int> = _callCount

        override fun generate(
            profile: UserProfile,
            recipes: List<RecipeSummary>,
            exclude: Set<String>
        ): DailyMealPlan? {
            calls++
            _callCount.value = calls
            lastExclude = exclude
            return result
        }
    }

    private class FakeUserRepository(initial: UserProfile?) : UserRepository {
        val profile = MutableStateFlow(initial)

        override fun getProfile(): Flow<UserProfile?> = profile
        override suspend fun saveProfile(profile: UserProfile) {
            this.profile.value = profile
        }
    }

    private class FakeRecipeRepository : RecipeRepository {
        override suspend fun getRecipes(): List<RecipeSummary> = emptyList()
        override suspend fun searchRecipes(query: String, difficulty: String, maxTimeMinutes: Int): List<RecipeSummary> = emptyList()
        override suspend fun getRecipeDetail(recipeId: String): RecipeDetail? = null
        override suspend fun getIngredients(): List<Ingredient> = emptyList()
        override suspend fun getCookingMethods(): List<CookingMethod> = emptyList()
    }

    private class FakeSeedStatus(initial: Boolean) : SeedStatus {
        private val _completed = MutableStateFlow(initial)
        override val seedCompleted: StateFlow<Boolean> = _completed
    }
}
