package com.example.smartcooking.domain.mealplan

import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile

interface MealPlanGenerator {
    fun generate(
        profile: UserProfile,
        recipes: List<RecipeSummary>,
        exclude: Set<String> = emptySet()
    ): DailyMealPlan?
}
