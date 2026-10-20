package com.example.smartcooking.domain.ai

import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile

interface AiRecipeAssistant {
    suspend fun recommend(
        profile: UserProfile,
        recipes: List<RecipeSummary>,
        exclude: Set<String> = emptySet()
    ): List<RecipeSummary>
}
