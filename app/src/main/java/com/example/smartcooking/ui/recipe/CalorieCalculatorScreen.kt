package com.example.smartcooking.ui.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.smartcooking.ui.components.DropdownSelector
import com.example.smartcooking.ui.components.EnergyCard
import java.util.Locale

@Composable
fun CalorieCalculatorScreen(viewModel: CalculatorViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Calorie Calculator", style = MaterialTheme.typography.headlineMedium)

        state.rows.forEachIndexed { index, row ->
            key(row.id) {
                Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Ingredient ${index + 1}", style = MaterialTheme.typography.titleSmall)

                        DropdownSelector(
                            label = "Select ingredient",
                            options = state.ingredients.map { it.ingredientId to it.name },
                            selectedId = row.ingredientId,
                            onSelect = { viewModel.onRowIngredientChange(row.id, it) }
                        )
                        OutlinedTextField(
                            value = row.weightG,
                            onValueChange = { viewModel.onRowWeightChange(row.id, it) },
                            label = { Text("Weight (g)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        DropdownSelector(
                            label = "Cooking method",
                            options = state.methods.map { it.methodId to it.methodName },
                            selectedId = row.methodId,
                            onSelect = { viewModel.onRowMethodChange(row.id, it) }
                        )
                        OutlinedTextField(
                            value = row.addedOilG,
                            onValueChange = { viewModel.onRowOilChange(row.id, it) },
                            label = { Text("Added oil (g)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        TextButton(
                            onClick = { viewModel.onRemoveRow(row.id) },
                            enabled = state.rows.size > 1,
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Text("Remove")
                        }
                    }
                }
            }
        }

        OutlinedButton(
            onClick = viewModel::onAddRow,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(100.dp)
        ) {
            Text("+ Add ingredient")
        }
        Button(
            onClick = viewModel::calculate,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(100.dp)
        ) {
            Text("Calculate Calories")
        }

        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        state.result?.let { result ->
            EnergyCard(
                label = "Total calories",
                value = String.format(Locale.US, "%.0f", result.totalCalories),
                unit = "kcal",
                stats = listOf(
                    "Protein" to String.format(Locale.US, "%.0f g", result.totalProteinG),
                    "Carbs" to String.format(Locale.US, "%.0f g", result.totalCarbsG),
                    "Fat" to String.format(Locale.US, "%.0f g", result.totalFatG)
                )
            )
        }
    }
}
