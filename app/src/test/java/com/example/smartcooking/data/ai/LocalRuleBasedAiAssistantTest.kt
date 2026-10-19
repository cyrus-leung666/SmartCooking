package com.example.smartcooking.data.ai

import com.example.smartcooking.domain.model.Gender
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.usecase.CalculateBmiUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalRuleBasedAiAssistantTest {

    private val assistant = LocalRuleBasedAiAssistant(CalculateBmiUseCase())

    @Test
    fun `recommends recipes within per meal budget band`() {
        // 男 24 175cm 70kg 1.375 → 目标 2308.28,单餐预算 769.4,区间 538.6~1000.2
        val profile = UserProfile(Gender.MALE, 24, 175.0, 70.0, 1.375)
        val recipes = listOf(
            RecipeSummary("r1", "凉拌黄瓜", "", 10, "简单", 44.5),
            RecipeSummary("r2", "番茄炒蛋", "", 15, "简单", 309.0),
            RecipeSummary("r3", "红烧牛肉炖土豆", "", 90, "困难", 589.8),
            RecipeSummary("r4", "香煎三文鱼配土豆", "", 25, "中等", 439.4)
        )

        val result = runBlocking { assistant.recommend(profile, recipes) }

        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.totalCalories >= 538.6 && it.totalCalories <= 1000.2 })
        assertEquals(listOf("红烧牛肉炖土豆"), result.map { it.title })
    }
}
