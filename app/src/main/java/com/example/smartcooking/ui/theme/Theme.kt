package com.example.smartcooking.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Extra brand surface for the "energy figure" hero cards (BMI / calories).
// White text reads well on both values.
val LocalEnergySurface = staticCompositionLocalOf { KiwiGreen }

private val LightColorScheme = lightColorScheme(
    primary = DeepGreen,
    onPrimary = Color.White,
    primaryContainer = MintTint,
    onPrimaryContainer = Color(0xFF06290F),
    secondary = Tangerine,
    onSecondary = Color.White,
    secondaryContainer = CreamPeach,
    onSecondaryContainer = Color(0xFF5C2E00),
    tertiary = BerryDeep,
    onTertiary = Color.White,
    tertiaryContainer = Blush,
    onTertiaryContainer = Color(0xFF5C0F22),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = MintWhite,
    onBackground = InkGreen,
    surface = Color.White,
    onSurface = InkGreen,
    surfaceVariant = FieldTint,
    onSurfaceVariant = InkSoft,
    outline = OutlineSoft,
    outlineVariant = OutlineFaint,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF2F8F3),
    surfaceContainer = Color(0xFFEDF3EE),
    surfaceContainerHigh = Color(0xFFE7EEE9),
    surfaceContainerHighest = Color(0xFFE1E9E3),
    inverseSurface = Color(0xFF2C3A30),
    inverseOnSurface = Color(0xFFEEF6EF),
    inversePrimary = Mint
)

private val DarkColorScheme = darkColorScheme(
    primary = Mint,
    onPrimary = Color(0xFF06290F),
    primaryContainer = Color(0xFF1E7A37),
    onPrimaryContainer = MintTint,
    secondary = Color(0xFFFFB267),
    onSecondary = Color(0xFF3A1E00),
    secondaryContainer = Color(0xFF6A3A00),
    onSecondaryContainer = CreamPeach,
    tertiary = Color(0xFFFF8BA0),
    onTertiary = Color(0xFF4E0719),
    tertiaryContainer = Color(0xFF7A1F34),
    onTertiaryContainer = Blush,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = ForestBlack,
    onBackground = Color(0xFFEDF6EF),
    surface = ForestSurface,
    onSurface = Color(0xFFEDF6EF),
    surfaceVariant = Color(0xFF23332A),
    onSurfaceVariant = Color(0xFFA9BDAF),
    outline = Color(0xFF3D4A41),
    outlineVariant = Color(0xFF2A3A30),
    surfaceContainerLowest = Color(0xFF0B1510),
    surfaceContainerLow = Color(0xFF131F17),
    surfaceContainer = Color(0xFF17241B),
    surfaceContainerHigh = Color(0xFF1B2A1F),
    surfaceContainerHighest = Color(0xFF1F3024),
    inverseSurface = Color(0xFFEDF6EF),
    inverseOnSurface = Color(0xFF2C3A30),
    inversePrimary = DeepGreen
)

@Composable
fun SmartCookingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalEnergySurface provides if (darkTheme) EnergySurfaceDark else KiwiGreen
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}
