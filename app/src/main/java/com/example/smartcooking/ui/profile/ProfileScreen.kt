package com.example.smartcooking.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.smartcooking.domain.model.Gender
import com.example.smartcooking.ui.components.EnergyCard
import java.util.Locale

private val ACTIVITY_LEVELS = listOf(
    1.2 to "Sedentary",
    1.375 to "Lightly Active",
    1.55 to "Moderately Active",
    1.725 to "Very Active"
)

private fun categoryLabel(category: String): String = when (category) {
    "Overweight" -> "Overweight · fat loss"
    "Underweight" -> "Underweight · weight gain"
    else -> "Normal · maintain"
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            snackbarHostState.showSnackbar("Profile saved")
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Personal Profile", style = MaterialTheme.typography.headlineMedium)

            state.result?.let { result ->
                EnergyCard(
                    label = "Your energy index",
                    value = String.format(Locale.US, "%.1f", result.bmi),
                    unit = "BMI",
                    chip = categoryLabel(result.category),
                    stats = listOf(
                        "Target" to String.format(Locale.US, "%,.0f kcal", result.targetCalories),
                        "BMR" to String.format(Locale.US, "%,.0f", result.bmr),
                        "TDEE" to String.format(Locale.US, "%,.0f", result.tdee)
                    )
                )
            }

            Text("Gender", style = MaterialTheme.typography.titleSmall)
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = state.gender == Gender.MALE,
                    onClick = { viewModel.onGenderChange(Gender.MALE) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text("Male") }
                SegmentedButton(
                    selected = state.gender == Gender.FEMALE,
                    onClick = { viewModel.onGenderChange(Gender.FEMALE) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text("Female") }
            }

            OutlinedTextField(
                value = state.age,
                onValueChange = viewModel::onAgeChange,
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = state.heightCm,
                onValueChange = viewModel::onHeightChange,
                label = { Text("Height (cm)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = state.weightKg,
                onValueChange = viewModel::onWeightChange,
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Activity level", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ACTIVITY_LEVELS.forEach { (level, label) ->
                    FilterChip(
                        selected = state.activityLevel == level,
                        onClick = { viewModel.onActivityChange(level) },
                        label = { Text(label) }
                    )
                }
            }

            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = viewModel::calculate,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text("Calculate Daily Calorie Target")
            }

            state.result?.let { result ->
                Card(Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "Macronutrients · ${result.nutrition.ratioLabel}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        MacroRow("Carbs", String.format(Locale.US, "%.0f g", result.nutrition.carbsG))
                        MacroRow("Protein", String.format(Locale.US, "%.0f g", result.nutrition.proteinG))
                        MacroRow("Fat", String.format(Locale.US, "%.0f g", result.nutrition.fatG))
                        Text(
                            result.nutrition.dietaryAdvice,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedButton(
                    onClick = viewModel::save,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text("Save Profile")
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun MacroRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(
            label,
            Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.labelLarge)
    }
}
