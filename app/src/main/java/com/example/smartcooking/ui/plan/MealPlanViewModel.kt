package com.example.smartcooking.ui.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcooking.domain.mealplan.DailyMealPlan
import com.example.smartcooking.domain.usecase.GenerateDailyMealPlanUseCase
import com.example.smartcooking.domain.usecase.GetUserProfileUseCase
import com.example.smartcooking.domain.usecase.ObserveSeedCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MealPlanUiState(
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val profileMissing: Boolean = false,
    val plan: DailyMealPlan? = null,
    val error: String? = null
)

@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val generateDailyMealPlanUseCase: GenerateDailyMealPlanUseCase,
    observeSeedCompletionUseCase: ObserveSeedCompletionUseCase,
    getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealPlanUiState())
    val uiState: StateFlow<MealPlanUiState> = _uiState.asStateFlow()

    private var seedCompleted = false
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            observeSeedCompletionUseCase().collect { completed ->
                seedCompleted = completed
                if (completed) load()
            }
        }
        viewModelScope.launch {
            getUserProfileUseCase()
                .distinctUntilChanged()
                .collect { profile ->
                    if (profile == null) {
                        loadJob?.cancel()
                        _uiState.update {
                            it.copy(
                                loading = false,
                                refreshing = false,
                                profileMissing = true,
                                plan = null,
                                error = null
                            )
                        }
                    } else {
                        _uiState.update { it.copy(profileMissing = false) }
                        load()
                    }
                }
        }
    }

    fun onRefresh() = refresh()

    fun regenerate() = refresh()

    private fun refresh() {
        if (_uiState.value.refreshing) return
        _uiState.update { it.copy(refreshing = true, error = null) }
        load()
    }

    private fun load() {
        loadJob?.cancel()
        _uiState.update { state ->
            if (state.profileMissing) {
                state
            } else {
                state.copy(loading = state.plan == null, error = null)
            }
        }
        loadJob = viewModelScope.launch {
            val exclude = _uiState.value.plan
                ?.meals
                ?.flatMap { meal -> meal.items }
                ?.map { it.recipeId }
                ?.toSet()
                .orEmpty()
            try {
                val plan = generateDailyMealPlanUseCase(exclude)
                _uiState.update { state ->
                    when {
                        plan != null -> state.copy(
                            loading = false,
                            refreshing = false,
                            profileMissing = false,
                            plan = plan,
                            error = null
                        )
                        state.profileMissing -> state.copy(
                            loading = false,
                            refreshing = false,
                            error = null
                        )
                        seedCompleted -> state.copy(
                            loading = false,
                            refreshing = false,
                            plan = null,
                            error = "Unable to generate a meal plan. Please check your profile and try again."
                        )
                        else -> state.copy(
                            loading = true,
                            refreshing = false,
                            error = null
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        refreshing = false,
                        error = "Failed to generate a meal plan, please try again later."
                    )
                }
            }
        }
    }
}
