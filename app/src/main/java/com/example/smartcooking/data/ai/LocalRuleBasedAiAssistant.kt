package com.example.smartcooking.data.ai

import com.example.smartcooking.domain.ai.AiRecipeAssistant
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.usecase.CalculateBmiUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

// AI 留桩:基于单餐预算(目标热量÷3)的本地规则推荐,后续可替换为 DeepSeek/OpenAI 远程实现。
// 推荐随机化:窗口候选不足时逐步放宽(±30% 起、每档 +10%,直到候选 ≥3 道或已包含全部菜谱),
// 优先排除当前已显示(exclude),再洗牌随机取 2 道
@Singleton
class LocalRuleBasedAiAssistant @Inject constructor(
    private val calculateBmiUseCase: CalculateBmiUseCase,
    private val random: Random
) : AiRecipeAssistant {

    override suspend fun recommend(
        profile: UserProfile,
        recipes: List<RecipeSummary>,
        exclude: Set<String>
    ): List<RecipeSummary> {
        if (recipes.isEmpty()) return emptyList()
        val target = calculateBmiUseCase(
            profile.gender,
            profile.age,
            profile.heightCm,
            profile.weightKg,
            profile.activityLevel
        ).targetCalories
        // 与"单餐预算"(全天目标 ÷3)比较,而非全天目标:
        // 单道菜热量(数十~数百 kcal)与全天预算量级不匹配,直接比会恒为空推荐
        val mealBudget = target / 3.0
        var pool = recipes.filter { it.totalCalories in mealBudget * 0.70..mealBudget * 1.30 }
        var tolerance = 0.30
        while (pool.size < 3 && pool.size < recipes.size) {
            tolerance += 0.10
            pool = recipes.filter { it.totalCalories in mealBudget * (1.0 - tolerance)..mealBudget * (1.0 + tolerance) }
        }
        // 尽量与当前显示的两道不同;排除后不足 2 道则允许重复
        val preferred = pool.filterNot { it.recipeId in exclude }
        val candidates = if (preferred.size >= 2) preferred else pool
        return candidates.shuffled(random).take(2)
    }
}
