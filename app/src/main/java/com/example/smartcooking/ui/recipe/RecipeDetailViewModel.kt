package com.example.smartcooking.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcooking.domain.usecase.GetRecipeDetailUseCase
import com.example.smartcooking.domain.usecase.RecipeDetailWithCalories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeDetailUiState(
    val loading: Boolean = true,
    val detail: RecipeDetailWithCalories? = null,
    val error: String? = null
)

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun load(recipeId: String) {
        if (_uiState.value.detail?.detail?.recipeId == recipeId) return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            try {
                val result = getRecipeDetailUseCase(recipeId)
                if (result == null) {
                    _uiState.update { it.copy(loading = false, error = "菜谱不存在") }
                } else {
                    _uiState.update { it.copy(loading = false, detail = result, error = null) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false, error = "加载失败,请稍后重试") }
            }
        }
    }
}
