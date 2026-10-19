package com.example.smartcooking.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.usecase.GetRecommendedRecipesUseCase
import com.example.smartcooking.domain.usecase.GetRecipesUseCase
import com.example.smartcooking.domain.usecase.ObserveSeedCompletionUseCase
import com.example.smartcooking.domain.usecase.SearchRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeListUiState(
    val query: String = "",
    val difficulty: String = "",
    val maxTimeMinutes: Int = 0,
    val recipes: List<RecipeSummary> = emptyList(),
    val recommended: List<RecipeSummary> = emptyList(),
    val loading: Boolean = true
)

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val getRecommendedRecipesUseCase: GetRecommendedRecipesUseCase,
    private val observeSeedCompletionUseCase: ObserveSeedCompletionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    init {
        refresh()
        // Task 4 审查修正:种子数据完成时刷新,消除首启"空列表粘滞"竞态
        viewModelScope.launch {
            observeSeedCompletionUseCase().collect { completed ->
                if (completed) refresh()
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            val recipes = getRecipesUseCase()
            val recommended = runCatching { getRecommendedRecipesUseCase() }.getOrDefault(emptyList())
            _uiState.update { it.copy(recipes = recipes, recommended = recommended, loading = false) }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        applyFilter()
    }

    fun onDifficultyChange(difficulty: String) {
        _uiState.update { it.copy(difficulty = difficulty) }
        applyFilter()
    }

    fun onMaxTimeChange(minutes: Int) {
        _uiState.update { it.copy(maxTimeMinutes = minutes) }
        applyFilter()
    }

    private fun applyFilter() {
        viewModelScope.launch {
            val state = _uiState.value
            val result = searchRecipesUseCase(state.query, state.difficulty, state.maxTimeMinutes)
            _uiState.update { it.copy(recipes = result) }
        }
    }
}
