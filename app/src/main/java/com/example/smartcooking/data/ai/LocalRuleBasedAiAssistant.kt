package com.example.smartcooking.data.ai

import com.example.smartcooking.domain.ai.AiRecipeAssistant
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.usecase.CalculateBmiUseCase
import javax.inject.Inject
import javax.inject.Singleton

// AI 留桩:基于目标热量 ±15% 的本地规则推荐,后续可替换为 DeepSeek/OpenAI 远程实现
@Singleton
class LocalRuleBasedAiAssistant @Inject constructor(
    private val calculateBmiUseCase: CalculateBmiUseCase
) : AiRecipeAssistant {

    override suspend fun recommend(userProfile: UserProfile, recipes: List<RecipeSummary>): List<RecipeSummary> {
        val target = calculateBmiUseCase(
            userProfile.gender,
            userProfile.age,
            userProfile.heightCm,
            userProfile.weightKg,
            userProfile.activityLevel
        ).targetCalories
        val min = target * 0.85
        val max = target * 1.15
        return recipes.filter { it.totalCalories in min..max }
    }
}
