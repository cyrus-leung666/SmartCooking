package com.example.smartcooking.domain.mealplan

import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile

/**
 * 生成一日三餐计划。实现可能执行 CPU 密集的组合搜索,调用方应在后台线程调用。
 * 返回 null 表示无档案/菜谱、目标热量非正、餐次池为空,或无法构造不重复的三餐。
 */
interface MealPlanGenerator {
    fun generate(
        profile: UserProfile,
        recipes: List<RecipeSummary>,
        exclude: Set<String> = emptySet()
    ): DailyMealPlan?
}
