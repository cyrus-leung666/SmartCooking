package com.example.smartcooking.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smartcooking.ui.theme.LocalEnergySurface

/**
 * The "energy figure" hero card — the signature element of the Fresh Energy
 * design language. Used anywhere the app surfaces a key number (BMI, daily
 * calorie target, total calories).
 */
@Composable
fun EnergyCard(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    chip: String? = null,
    stats: List<Pair<String, String>> = emptyList()
) {
    Surface(
        color = LocalEnergySurface.current,
        shape = MaterialTheme.shapes.large,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    value,
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White
                )
                Text(
                    unit,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
                )
            }
            if (chip != null) {
                Surface(
                    color = Color.White.copy(alpha = 0.22f),
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        chip,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            if (stats.isNotEmpty()) {
                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    stats.forEach { (key, statValue) ->
                        Column {
                            Text(
                                key,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                statValue,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
