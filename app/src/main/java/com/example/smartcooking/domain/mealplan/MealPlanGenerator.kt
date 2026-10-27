package com.example.smartcooking.domain.mealplan

import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile

/**
 * 生成一日三餐计划。实现可能执行 CPU 密集的组合搜索,调用方应在后台线程调用。
 * 返回 null 表示无档案/菜谱、目标热量非正、餐次池为空,或在有界搜索与多样性补救后
 * 仍无法构造不重复的三餐(例如多个餐池只包含同一道菜)。
 */
interface MealPlanGenerator {
    fun generate(
        profile: UserProfile,
        recipes: List<RecipeSummary>,
        exclude: Set<String> = emptySet()
    ): DailyMealPlan?
}
