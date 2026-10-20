package com.example.smartcooking.ui.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartcooking.domain.usecase.RecipeDetailWithCalories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onBack: () -> Unit,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(recipeId) { viewModel.load(recipeId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.detail?.detail?.title ?: "菜谱详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.loading -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.error != null -> Text(
                state.error ?: "",
                Modifier.padding(innerPadding).padding(16.dp)
            )

            state.detail != null -> DetailContent(state.detail!!, Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun DetailContent(detail: RecipeDetailWithCalories, modifier: Modifier = Modifier) {
    val d = detail.detail
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (d.coverImageUrl.isNotBlank()) {
            AsyncImage(
                model = d.coverImageUrl,
                contentDescription = d.title,
                modifier = Modifier.fillMaxWidth().height(160.dp),
                contentScale = ContentScale.Crop
            )
        }
        Text("${d.cookingTimeMinutes} 分钟 · ${d.difficulty}", style = MaterialTheme.typography.bodySmall)
        Text(d.description, style = MaterialTheme.typography.bodyMedium)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("热量概览", style = MaterialTheme.typography.titleSmall)
                Text(
                    "总热量 %.1f kcal".format(detail.calories.totalCalories),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "蛋白质 %.1f g · 碳水 %.1f g · 脂肪 %.1f g".format(
                        detail.calories.totalProteinG,
                        detail.calories.totalCarbsG,
                        detail.calories.totalFatG
                    )
                )
            }
        }

        Text("食材明细", style = MaterialTheme.typography.titleMedium)
        d.items.forEach { item ->
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(item.name)
                    Text(
                        buildString {
                            append("%.0f g · %s".format(item.weightG, item.methodName))
                            if (item.addedOilG > 0) append(" · 加油 %.0f g".format(item.addedOilG))
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    "%.1f kcal".format(item.caloriesPer100g * item.weightG / 100.0),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text("热量明细", style = MaterialTheme.typography.titleMedium)
        detail.calories.breakdown.forEach { entry ->
            Row(Modifier.fillMaxWidth()) {
                Text(entry.name, Modifier.weight(1f))
                Text("%.1f kcal".format(entry.calories))
            }
        }

        Text("烹饪步骤", style = MaterialTheme.typography.titleMedium)
        d.instructions.forEachIndexed { index, step ->
            Text("${index + 1}. $step", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
