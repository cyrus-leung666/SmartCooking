package com.example.smartcooking.ui.theme

import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// SmartCooking design tokens — "Fresh Energy" (元气餐桌)
//
// The palette is grounded in fresh produce rather than the generic
// "green + white" health look: kiwi / leaf greens for the brand, mango
// orange for energy, berry red for alerts, and green-tinted neutrals so
// surfaces never read as dead white or pure black.
//
// For the full rationale and the semantic mapping, see DESIGN.md at the
// project root.
// ---------------------------------------------------------------------------

// --- Brand accents ---
val KiwiGreen = Color(0xFF2BA24A)   // vivid brand green — hero / energy surfaces
val Mango = Color(0xFFFF8A3C)       // energy accent
val Berry = Color(0xFFE94F6C)       // alert / highlight accent

// --- Light palette ---
val DeepGreen = Color(0xFF1F7A3D)   // legible primary (body text on white)
val MintWhite = Color(0xFFF6FBF7)   // background
val InkGreen = Color(0xFF1C2A21)    // main text
val InkSoft = Color(0xFF52655A)     // secondary text
val MintTint = Color(0xFFC9F0D6)    // primary container
val Tangerine = Color(0xFFD96A1F)   // legible secondary (deeper than Mango)
val CreamPeach = Color(0xFFFFE3CC)  // secondary container
val BerryDeep = Color(0xFFC22E4F)   // legible tertiary (deeper than Berry)
val Blush = Color(0xFFFFD7E0)       // tertiary container
val FieldTint = Color(0xFFEAF3EC)   // surface variant
val OutlineSoft = Color(0xFFC7D5CA) // outline
val OutlineFaint = Color(0xFFDFEAE2)// outline variant

// --- Dark palette ---
val Mint = Color(0xFF57C97B)        // primary (brightened for dark surfaces)
val ForestBlack = Color(0xFF101B14) // background
val ForestSurface = Color(0xFF17241B) // surface
val EnergySurfaceDark = Color(0xFF1E7A37) // hero / energy figure fill (dark)
