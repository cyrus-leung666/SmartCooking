package com.example.smartcooking.data.ai

import com.example.smartcooking.domain.ai.AiRecipeAssistant
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.usecase.CalculateBmiUseCase
import javax.inject.Inject
import javax.inject.Singleton

// AI 留桩:基于单餐预算(目标热量÷3)±30% 的本地规则推荐,后续可替换为 DeepSeek/OpenAI 远程实现
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
        // 与"单餐预算"(全天目标 ÷3)比较,而非全天目标:
        // 单道菜热量(数十~数百 kcal)与全天预算量级不匹配,直接比会恒为空推荐
        val mealBudget = target / 3.0
        val min = mealBudget * 0.70
        val max = mealBudget * 1.30
        return recipes.filter { it.totalCalories in min..max }
    }
}
