package com.example.smartcooking.ui.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.smartcooking.ui.components.DropdownSelector
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
        Text("热量计算器", style = MaterialTheme.typography.headlineMedium)

        state.rows.forEach { row ->
            key(row.id) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DropdownSelector(
                            label = "选择食材",
                            options = state.ingredients.map { it.ingredientId to it.name },
                            selectedId = row.ingredientId,
                            onSelect = { viewModel.onRowIngredientChange(row.id, it) }
                        )
                        OutlinedTextField(
                            value = row.weightG,
                            onValueChange = { viewModel.onRowWeightChange(row.id, it) },
                            label = { Text("克重 (g)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        DropdownSelector(
                            label = "烹饪方式",
                            options = state.methods.map { it.methodId to it.methodName },
                            selectedId = row.methodId,
                            onSelect = { viewModel.onRowMethodChange(row.id, it) }
                        )
                        OutlinedTextField(
                            value = row.addedOilG,
                            onValueChange = { viewModel.onRowOilChange(row.id, it) },
                            label = { Text("额外加油 (g)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        TextButton(
                            onClick = { viewModel.onRemoveRow(row.id) },
                            enabled = state.rows.size > 1
                        ) {
                            Text("删除该食材")
                        }
                    }
                }
            }
        }

        OutlinedButton(onClick = viewModel::onAddRow, modifier = Modifier.fillMaxWidth()) {
            Text("+ 添加食材")
        }
        Button(onClick = viewModel::calculate, modifier = Modifier.fillMaxWidth()) {
            Text("计算热量")
        }

        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        state.result?.let { result ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("计算结果", style = MaterialTheme.typography.titleMedium)
                    Text(
                        String.format(Locale.US, "总热量 %.1f kcal", result.totalCalories),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        String.format(
                            Locale.US,
                            "蛋白质 %.1f g · 碳水 %.1f g · 脂肪 %.1f g",
                            result.totalProteinG,
                            result.totalCarbsG,
                            result.totalFatG
                        )
                    )
                    result.breakdown.forEach { entry ->
                        Row(Modifier.fillMaxWidth()) {
                            Text(entry.name, Modifier.weight(1f))
                            Text(String.format(Locale.US, "%.1f kcal", entry.calories))
                        }
                    }
                }
            }
        }
    }
}
