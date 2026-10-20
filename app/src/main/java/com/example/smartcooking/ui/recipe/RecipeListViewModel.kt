package com.example.smartcooking.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.usecase.GetRecommendedRecipesUseCase
import com.example.smartcooking.domain.usecase.GetUserProfileUseCase
import com.example.smartcooking.domain.usecase.ObserveSeedCompletionUseCase
import com.example.smartcooking.domain.usecase.SearchRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeListUiState(
    val query: String = "",
    val difficulty: String = "",
    val maxTimeMinutes: Int = 0,
    val recipes: List<RecipeSummary> = emptyList(),
    val recommended: List<RecipeSummary> = emptyList(),
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val getRecommendedRecipesUseCase: GetRecommendedRecipesUseCase,
    private val observeSeedCompletionUseCase: ObserveSeedCompletionUseCase,
    getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    private var filterJob: Job? = null

    init {
        refresh()
        // Task 4 审查修正:种子数据完成时刷新,消除首启"空列表粘滞"竞态
        viewModelScope.launch {
            observeSeedCompletionUseCase().collect { completed ->
                if (completed) refresh()
            }
        }
        // 档案联动:保存新档案后自动重算推荐,无需重启;首启档案晚到时也会补算
        viewModelScope.launch {
            getUserProfileUseCase()
                .distinctUntilChanged()
                .collect { profile ->
                    if (profile != null) refreshRecommendations()
                }
        }
    }

    /** 下拉刷新:重载全部列表 + 重新随机推荐 */
    fun onRefresh() {
        if (_uiState.value.refreshing) return
        load(asRefresh = true)
    }

    /** 首启与种子完成:不带下拉指示的静默刷新 */
    private fun refresh() = load()

    /** 档案变化:仅重算推荐区 */
    private fun refreshRecommendations() = load(recommendationsOnly = true)

    private fun load(asRefresh: Boolean = false, recommendationsOnly: Boolean = false) {
        viewModelScope.launch {
            if (asRefresh) _uiState.update { it.copy(refreshing = true) }
            try {
                val state = _uiState.value
                val recommended = loadRecommendations(state)
                if (recommendationsOnly) {
                    _uiState.update { it.copy(recommended = recommended, error = null) }
                } else {
                    val recipes = searchRecipesUseCase(state.query, state.difficulty, state.maxTimeMinutes)
                    _uiState.update {
                        it.copy(
                            recipes = recipes,
                            recommended = recommended,
                            loading = false,
                            refreshing = false,
                            error = null
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loading = false, refreshing = false, error = "Failed to load, please try again later")
                }
            }
        }
    }

    /** 推荐读取失败时降级为空(与既有行为一致);exclude = 当前已显示的推荐,尽量出新 */
    private suspend fun loadRecommendations(state: RecipeListUiState): List<RecipeSummary> =
        try {
            getRecommendedRecipesUseCase(state.recommended.map { it.recipeId }.toSet())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emptyList()
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
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            delay(300)
            try {
                val state = _uiState.value
                val result = searchRecipesUseCase(state.query, state.difficulty, state.maxTimeMinutes)
                _uiState.update { it.copy(recipes = result, error = null) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load, please try again later") }
            }
        }
    }
}
