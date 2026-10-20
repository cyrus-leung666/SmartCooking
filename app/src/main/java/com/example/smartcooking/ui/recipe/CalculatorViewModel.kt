package com.example.smartcooking.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcooking.domain.model.CalorieResult
import com.example.smartcooking.domain.model.CalculatorItem
import com.example.smartcooking.domain.model.CookingMethod
import com.example.smartcooking.domain.model.Ingredient
import com.example.smartcooking.domain.usecase.CalculateRecipeCalorieUseCase
import com.example.smartcooking.domain.usecase.GetCookingMethodsUseCase
import com.example.smartcooking.domain.usecase.GetIngredientsUseCase
import com.example.smartcooking.domain.usecase.ObserveSeedCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalculatorRow(
    val id: Int,
    val ingredientId: String,
    val weightG: String = "100",
    val methodId: String,
    val addedOilG: String = "0"
)

data class CalculatorUiState(
    val ingredients: List<Ingredient> = emptyList(),
    val methods: List<CookingMethod> = emptyList(),
    val rows: List<CalculatorRow> = emptyList(),
    val result: CalorieResult? = null,
    val error: String? = null
)

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val getIngredientsUseCase: GetIngredientsUseCase,
    private val getCookingMethodsUseCase: GetCookingMethodsUseCase,
    private val calculateRecipeCalorieUseCase: CalculateRecipeCalorieUseCase,
    private val observeSeedCompletionUseCase: ObserveSeedCompletionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var nextRowId = 1

    private var loadJob: Job? = null

    init {
        loadData()
        // 种子数据完成时重载,修复首启竞态下食材/方式列表为空
        viewModelScope.launch {
            observeSeedCompletionUseCase().collect { completed ->
                if (completed) loadData()
            }
        }
    }

    private fun loadData() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            try {
                val ingredients = getIngredientsUseCase()
                val methods = getCookingMethodsUseCase()
                val firstRow = CalculatorRow(
                    id = 0,
                    ingredientId = ingredients.firstOrNull()?.ingredientId.orEmpty(),
                    weightG = "100",
                    methodId = methods.firstOrNull()?.methodId.orEmpty(),
                    addedOilG = "0"
                )
                _uiState.update { state ->
                    val needsReseed = state.rows.isEmpty() ||
                        state.rows.all { it.ingredientId.isEmpty() && it.methodId.isEmpty() }
                    state.copy(
                        ingredients = ingredients,
                        methods = methods,
                        rows = if (needsReseed) listOf(firstRow) else state.rows,
                        error = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "数据加载失败,请稍后重试") }
            }
        }
    }

    private fun newRow(id: Int): CalculatorRow {
        val state = _uiState.value
        return CalculatorRow(
            id = id,
            ingredientId = state.ingredients.firstOrNull()?.ingredientId.orEmpty(),
            weightG = "100",
            methodId = state.methods.firstOrNull()?.methodId.orEmpty(),
            addedOilG = "0"
        )
    }

    fun onAddRow() {
        val id = nextRowId++
        _uiState.update { it.copy(rows = it.rows + newRow(id), result = null) }
    }

    fun onRemoveRow(rowId: Int) {
        _uiState.update { state ->
            if (state.rows.size <= 1) state
            else state.copy(rows = state.rows.filterNot { it.id == rowId }, result = null)
        }
    }

    fun onRowIngredientChange(rowId: Int, ingredientId: String) =
        updateRow(rowId) { it.copy(ingredientId = ingredientId) }

    fun onRowWeightChange(rowId: Int, weight: String) =
        updateRow(rowId) { it.copy(weightG = weight) }

    fun onRowMethodChange(rowId: Int, methodId: String) =
        updateRow(rowId) { it.copy(methodId = methodId) }

    fun onRowOilChange(rowId: Int, oil: String) =
        updateRow(rowId) { it.copy(addedOilG = oil) }

    private fun updateRow(rowId: Int, transform: (CalculatorRow) -> CalculatorRow) {
        _uiState.update { state ->
            state.copy(
                rows = state.rows.map { row -> if (row.id == rowId) transform(row) else row },
                result = null
            )
        }
    }

    fun calculate() {
        val state = _uiState.value
        val items = state.rows.mapNotNull { row ->
            val ingredient = state.ingredients.firstOrNull { it.ingredientId == row.ingredientId }
                ?: return@mapNotNull null
            val method = state.methods.firstOrNull { it.methodId == row.methodId }
                ?: return@mapNotNull null
            val weight = row.weightG.toDoubleOrNull() ?: return@mapNotNull null
            if (!weight.isFinite() || weight <= 0.0) return@mapNotNull null
            val oil = row.addedOilG.toDoubleOrNull() ?: return@mapNotNull null
            if (!oil.isFinite()) return@mapNotNull null
            CalculatorItem(ingredient, weight, method, oil)
        }
        if (items.size != state.rows.size) {
            _uiState.update { it.copy(error = "请完整填写每一行的食材、克重和烹饪方式", result = null) }
            return
        }
        val result = calculateRecipeCalorieUseCase(items)
        _uiState.update { it.copy(result = result, error = null) }
    }
}
