package com.example.smartcooking.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.ui.recipe.recipeCoverImageRes
import java.util.Locale

/** Difficulty is stored in Chinese but displayed in English. */
fun difficultyLabel(difficulty: String): String = when (difficulty) {
    "简单" -> "Easy"
    "中等" -> "Medium"
    "困难" -> "Hard"
    else -> difficulty
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCard(
    recipe: RecipeSummary,
    recommended: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RecipeThumbnail(recipe, Modifier.size(56.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${recipe.cookingTimeMinutes} min · ${difficultyLabel(recipe.difficulty)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (recommended) {
                    Spacer(Modifier.height(5.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            "Recommended",
                            Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }
            Text(
                String.format(Locale.US, "%.0f kcal", recipe.totalCalories),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RecipeThumbnail(recipe: RecipeSummary, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    val res = recipeCoverImageRes(recipe.recipeId)
    when {
        res != null -> Image(
            painter = painterResource(res),
            contentDescription = recipe.title,
            modifier = modifier.clip(shape),
            contentScale = ContentScale.Crop
        )
        recipe.coverImageUrl.isNotBlank() -> AsyncImage(
            model = recipe.coverImageUrl,
            contentDescription = recipe.title,
            modifier = modifier.clip(shape),
            contentScale = ContentScale.Crop
        )
        else -> Box(
            modifier.clip(shape).background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}
