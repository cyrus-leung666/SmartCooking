package com.example.smartcooking.ui.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.smartcooking.ui.components.RecipeCard

// 难度 chips:值保持中文存储值,仅显示文案英文
private val DIFFICULTY_OPTIONS = listOf(
    "" to "All difficulties",
    "简单" to "Easy",
    "中等" to "Medium",
    "困难" to "Hard"
)

private val TIME_OPTIONS = listOf(
    0 to "Any duration",
    15 to "≤15 min",
    30 to "≤30 min",
    60 to "≤60 min",
    120 to "≤2 h"
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    onRecipeClick: (String) -> Unit,
    viewModel: RecipeListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Recipe Library", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::onQueryChange,
            label = { Text("Search recipes or ingredients, e.g. 番茄") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DIFFICULTY_OPTIONS.forEach { (value, label) ->
                FilterChip(
                    selected = state.difficulty == value,
                    onClick = { viewModel.onDifficultyChange(value) },
                    label = { Text(label) }
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TIME_OPTIONS.forEach { (value, label) ->
                FilterChip(
                    selected = state.maxTimeMinutes == value,
                    onClick = { viewModel.onMaxTimeChange(value) },
                    label = { Text(label) }
                )
            }
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        PullToRefreshBox(
            isRefreshing = state.refreshing,
            onRefresh = viewModel::onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
            if (state.recommended.isNotEmpty()) {
                item {
                    Text("Recommended for you (based on your BMI calorie target)", style = MaterialTheme.typography.titleMedium)
                }
                items(state.recommended, key = { "rec_${it.recipeId}" }) { recipe ->
                    RecipeCard(recipe = recipe, recommended = true, onClick = { onRecipeClick(recipe.recipeId) })
                }
                item {
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                }
            }
            if (state.loading) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            items(state.recipes, key = { it.recipeId }) { recipe ->
                RecipeCard(recipe = recipe, onClick = { onRecipeClick(recipe.recipeId) })
            }
            if (!state.loading && state.recipes.isEmpty()) {
                item { Text("No matching recipes") }
            }
            }
        }
    }
}
