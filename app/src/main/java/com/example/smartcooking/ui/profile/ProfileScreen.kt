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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartcooking.domain.model.Gender

private val ACTIVITY_LEVELS = listOf(
    1.2 to "久坐",
    1.375 to "轻度活动",
    1.55 to "中度活动",
    1.725 to "高强度"
)

private fun categoryLabel(category: String): String = when (category) {
    "Overweight" -> "超重(减脂)"
    "Underweight" -> "偏瘦(增重)"
    else -> "正常(维持)"
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
        Text("个人档案", style = MaterialTheme.typography.headlineMedium)

        Text("性别", style = MaterialTheme.typography.titleSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.gender == Gender.MALE,
                    onClick = { viewModel.onGenderChange(Gender.MALE) }
                )
                Text("男")
            }
            Spacer(Modifier.width(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.gender == Gender.FEMALE,
                    onClick = { viewModel.onGenderChange(Gender.FEMALE) }
                )
                Text("女")
            }
        }

        OutlinedTextField(
            value = state.age,
            onValueChange = viewModel::onAgeChange,
            label = { Text("年龄") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.heightCm,
            onValueChange = viewModel::onHeightChange,
            label = { Text("身高 (cm)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.weightKg,
            onValueChange = viewModel::onWeightChange,
            label = { Text("体重 (kg)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text("活动系数", style = MaterialTheme.typography.titleSmall)
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
            Text("计算每日推荐热量")
        }

        state.result?.let { result ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("计算结果", style = MaterialTheme.typography.titleMedium)
                    Text("BMI: ${result.bmi}(${categoryLabel(result.category)})")
                    Text("BMR: ${result.bmr} kcal")
                    Text("TDEE: ${result.tdee} kcal")
                    Text(
                        "每日推荐热量: ${result.targetCalories} kcal",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text("三大营养素配比 ${result.nutrition.ratioLabel}")
                    Text(
                        "碳水 ${result.nutrition.carbsG} g / 蛋白质 ${result.nutrition.proteinG} g / 脂肪 ${result.nutrition.fatG} g"
                    )
                    Text(result.nutrition.dietaryAdvice, style = MaterialTheme.typography.bodySmall)
                }
            }

            OutlinedButton(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                Text("保存个人档案")
            }
            if (state.saved) {
                Text("已保存", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
