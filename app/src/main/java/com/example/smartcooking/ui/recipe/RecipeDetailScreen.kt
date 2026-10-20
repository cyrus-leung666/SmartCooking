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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartcooking.domain.usecase.RecipeDetailWithCalories
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onBack: () -> Unit,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val detail = state.detail
    val error = state.error

    LaunchedEffect(recipeId) { viewModel.load(recipeId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.detail?.detail?.title ?: "Recipe Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

            error != null -> Text(
                error,
                Modifier.padding(innerPadding).padding(16.dp),
                color = MaterialTheme.colorScheme.error
            )

            detail != null -> DetailContent(detail, Modifier.padding(innerPadding))

            else -> Box(Modifier.fillMaxSize().padding(innerPadding))
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
        Text("${d.cookingTimeMinutes} min · ${d.difficulty}", style = MaterialTheme.typography.bodySmall)
        Text(d.description, style = MaterialTheme.typography.bodyMedium)

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Calorie Overview", style = MaterialTheme.typography.titleSmall)
                Text(
                    String.format(Locale.US, "Total %.1f kcal", detail.calories.totalCalories),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    String.format(
                        Locale.US,
                        "Protein %.1f g · Carbs %.1f g · Fat %.1f g",
                        detail.calories.totalProteinG,
                        detail.calories.totalCarbsG,
                        detail.calories.totalFatG
                    )
                )
            }
        }

        Text("Ingredients", style = MaterialTheme.typography.titleMedium)
        d.items.forEach { item ->
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(item.name)
                    Text(
                        buildString {
                            append(String.format(Locale.US, "%.0f g · %s", item.weightG, item.methodName))
                            if (item.addedOilG > 0) {
                                append(String.format(Locale.US, " · +%.0f g oil", item.addedOilG))
                            }
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    String.format(Locale.US, "%.1f kcal", item.caloriesPer100g * item.weightG / 100.0),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text("Calorie Breakdown", style = MaterialTheme.typography.titleMedium)
        detail.calories.breakdown.forEach { entry ->
            Row(Modifier.fillMaxWidth()) {
                Text(entry.name, Modifier.weight(1f))
                Text(String.format(Locale.US, "%.1f kcal", entry.calories))
            }
        }

        Text("Cooking Steps", style = MaterialTheme.typography.titleMedium)
        d.instructions.forEachIndexed { index, step ->
            Text("${index + 1}. $step", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
