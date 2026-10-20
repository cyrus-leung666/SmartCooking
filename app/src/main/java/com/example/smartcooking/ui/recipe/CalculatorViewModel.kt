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
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val calculateRecipeCalorieUseCase: CalculateRecipeCalorieUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private var nextRowId = 1

    init {
        viewModelScope.launch {
            val ingredients = getIngredientsUseCase()
            val methods = getCookingMethodsUseCase()
            _uiState.update {
                it.copy(
                    ingredients = ingredients,
                    methods = methods,
                    rows = listOf(newRow(0))
                )
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
        _uiState.update { it.copy(rows = it.rows + newRow(nextRowId++)) }
    }

    fun onRemoveRow(rowId: Int) {
        _uiState.update { it.copy(rows = it.rows.filterNot { row -> row.id == rowId }) }
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
            state.copy(rows = state.rows.map { row -> if (row.id == rowId) transform(row) else row })
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
            val oil = row.addedOilG.toDoubleOrNull() ?: return@mapNotNull null
            if (weight <= 0.0) return@mapNotNull null
            CalculatorItem(ingredient, weight, method, oil)
        }
        if (items.size != state.rows.size) {
            _uiState.update { it.copy(error = "请完整填写每一行的食材、克重和烹饪方式") }
            return
        }
        val result = calculateRecipeCalorieUseCase(items)
        _uiState.update { it.copy(result = result, error = null) }
    }
}
