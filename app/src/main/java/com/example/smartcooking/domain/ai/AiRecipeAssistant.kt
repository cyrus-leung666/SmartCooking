package com.example.smartcooking.domain.ai

import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile

interface AiRecipeAssistant {
    suspend fun recommend(userProfile: UserProfile, recipes: List<RecipeSummary>): List<RecipeSummary>
}
