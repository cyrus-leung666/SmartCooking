package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.repository.RecipeRepository
import javax.inject.Inject

class SearchRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    /**
     * @param query 搜索关键词,空串表示不限
     * @param difficulty 难度筛选("简单"/"中等"/"困难"),空串表示不限
     * @param maxTimeMinutes 时长上限(分钟),0 表示不限时长
     */
    suspend operator fun invoke(query: String, difficulty: String, maxTimeMinutes: Int): List<RecipeSummary> =
        recipeRepository.searchRecipes(query.trim(), difficulty, maxTimeMinutes)
}
