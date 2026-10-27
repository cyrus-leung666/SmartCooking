package com.example.smartcooking.ui.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartcooking.domain.model.RecipeDetail
import com.example.smartcooking.domain.usecase.RecipeDetailWithCalories
import com.example.smartcooking.ui.components.EnergyCard
import com.example.smartcooking.ui.components.difficultyLabel
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

    Scaffold { innerPadding ->
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

            detail != null -> DetailContent(detail, onBack, Modifier.padding(innerPadding))

            else -> Box(Modifier.fillMaxSize().padding(innerPadding))
        }
    }
}

@Composable
private fun DetailContent(
    detail: RecipeDetailWithCalories,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val d = detail.detail
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box {
            CoverImage(d)
            IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }

        Text(d.title, style = MaterialTheme.typography.headlineSmall)

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            InfoChip(if (d.cookingTimeMinutes > 0) "${d.cookingTimeMinutes} min" else "Ready to eat")
            InfoChip(difficultyLabel(d.difficulty))
            InfoChip(String.format(Locale.US, "%.0f kcal", detail.calories.totalCalories))
        }

        if (d.description.isNotBlank()) {
            Text(
                d.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        EnergyCard(
            label = "Total calories",
            value = String.format(Locale.US, "%.0f", detail.calories.totalCalories),
            unit = "kcal",
            stats = listOf(
                "Protein" to String.format(Locale.US, "%.0f g", detail.calories.totalProteinG),
                "Carbs" to String.format(Locale.US, "%.0f g", detail.calories.totalCarbsG),
                "Fat" to String.format(Locale.US, "%.0f g", detail.calories.totalFatG)
            )
        )

        Text("Ingredients", style = MaterialTheme.typography.titleMedium)
        Card(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                d.items.forEach { item ->
                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) {
                            Text(item.name, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                buildString {
                                    append(String.format(Locale.US, "%.0f g · %s", item.weightG, item.methodName))
                                    if (item.addedOilG > 0) {
                                        append(String.format(Locale.US, " · +%.0f g oil", item.addedOilG))
                                    }
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            String.format(Locale.US, "%.0f kcal", item.caloriesPer100g * item.weightG / 100.0),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Text("Cooking steps", style = MaterialTheme.typography.titleMedium)
        d.instructions.forEachIndexed { index, step ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${index + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    step,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) {
        Text(
            text,
            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun CoverImage(d: RecipeDetail) {
    val shape = MaterialTheme.shapes.large
    val res = recipeCoverImageRes(d.recipeId)
    when {
        res != null -> Image(
            painter = painterResource(res),
            contentDescription = d.title,
            modifier = Modifier.fillMaxWidth().height(200.dp).clip(shape),
            contentScale = ContentScale.Crop
        )
        d.coverImageUrl.isNotBlank() -> AsyncImage(
            model = d.coverImageUrl,
            contentDescription = d.title,
            modifier = Modifier.fillMaxWidth().height(200.dp).clip(shape),
            contentScale = ContentScale.Crop
        )
    }
}
