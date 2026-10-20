package com.example.smartcooking.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.smartcooking.domain.model.Gender

private val ACTIVITY_LEVELS = listOf(
    1.2 to "Sedentary",
    1.375 to "Lightly Active",
    1.55 to "Moderately Active",
    1.725 to "Very Active"
)

private fun categoryLabel(category: String): String = when (category) {
    "Overweight" -> "Overweight (fat loss)"
    "Underweight" -> "Underweight (weight gain)"
    else -> "Normal (maintain)"
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Personal Profile", style = MaterialTheme.typography.headlineMedium)

        Text("Gender", style = MaterialTheme.typography.titleSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.gender == Gender.MALE,
                    onClick = { viewModel.onGenderChange(Gender.MALE) }
                )
                Text("Male")
            }
            Spacer(Modifier.width(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.gender == Gender.FEMALE,
                    onClick = { viewModel.onGenderChange(Gender.FEMALE) }
                )
                Text("Female")
            }
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

        Text("Activity Level", style = MaterialTheme.typography.titleSmall)
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

        Button(onClick = viewModel::calculate, modifier = Modifier.fillMaxWidth()) {
            Text("Calculate Daily Calorie Target")
        }

        state.result?.let { result ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Results", style = MaterialTheme.typography.titleMedium)
                    Text("BMI: ${result.bmi} (${categoryLabel(result.category)})")
                    Text("BMR: ${result.bmr} kcal")
                    Text("TDEE: ${result.tdee} kcal")
                    Text(
                        "Daily Calorie Target: ${result.targetCalories} kcal",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text("Macronutrient Ratio ${result.nutrition.ratioLabel}")
                    Text(
                        "Carbs ${result.nutrition.carbsG} g / Protein ${result.nutrition.proteinG} g / Fat ${result.nutrition.fatG} g"
                    )
                    Text(result.nutrition.dietaryAdvice, style = MaterialTheme.typography.bodySmall)
                }
            }

            OutlinedButton(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                Text("Save Profile")
            }
            if (state.saved) {
                Text("Saved", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
