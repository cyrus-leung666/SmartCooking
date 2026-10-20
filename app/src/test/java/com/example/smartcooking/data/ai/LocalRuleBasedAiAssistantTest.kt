package com.example.smartcooking.data.ai

import com.example.smartcooking.domain.model.Gender
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.usecase.CalculateBmiUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class LocalRuleBasedAiAssistantTest {

    // 男 24 175cm 70kg 1.375 → 目标 2308.28,单餐预算 769.43
    private val profile = UserProfile(Gender.MALE, 24, 175.0, 70.0, 1.375)
    private val recipes = listOf(
        RecipeSummary("r1", "凉拌黄瓜", "", 10, "简单", 44.5),
        RecipeSummary("r2", "番茄炒蛋", "", 15, "简单", 309.0),
        RecipeSummary("r3", "红烧牛肉炖土豆", "", 90, "困难", 589.8),
        RecipeSummary("r4", "香煎三文鱼配土豆", "", 25, "中等", 439.4)
    )

    private fun assistant(seed: Int = 42) =
        LocalRuleBasedAiAssistant(CalculateBmiUseCase(), Random(seed))

    @Test
    fun `recommends exactly two recipes within final tolerance band`() {
        // ±30%(538.6~1000.3)仅 1 道(589.8);放宽至 ±60%(307.8~1231.1)凑足 3 道候选后随机取 2
        val result = runBlocking { assistant().recommend(profile, recipes) }

        assertEquals(2, result.size)
        assertTrue(result.all { it.totalCalories in 307.7..1231.1 })
        assertTrue(result.all { it.recipeId in setOf("r2", "r3", "r4") })
    }

    @Test
    fun `prefers candidates not in exclude when enough remain`() {
        val result = runBlocking { assistant().recommend(profile, recipes, exclude = setOf("r4")) }

        // 排除 r4 后候选仍有 r2、r3 两道 → 结果必为这两道
        assertEquals(setOf("r2", "r3"), result.map { it.recipeId }.toSet())
    }

    @Test
    fun `falls back to repeats when exclude would leave fewer than two`() {
        val result = runBlocking {
            assistant().recommend(profile, recipes, exclude = setOf("r2", "r3", "r4"))
        }

        assertEquals(2, result.size)
        assertTrue(result.all { it.recipeId in setOf("r2", "r3", "r4") })
    }

    @Test
    fun `keeps widening past exactly two candidates so refresh can produce new picks`() {
        // 若放宽停在恰好 2 道(±40%~±50% 时窗口内仅 r3、r4),任何随机结果都只含 r3、r4;
        // 继续放宽至 ≥3 道后 r2 才有机会出现。20 个种子下 r2 一次都不出现的概率 (1/3)^20 ≈ 0
        val anySeedPickedR2 = (1..20).any { seed ->
            runBlocking { assistant(seed).recommend(profile, recipes) }
                .any { it.recipeId == "r2" }
        }
        assertTrue(anySeedPickedR2)
    }

    @Test
    fun `same seed produces same result`() {
        val first = runBlocking { assistant(42).recommend(profile, recipes) }
        val second = runBlocking { assistant(42).recommend(profile, recipes) }

        assertEquals(first, second)
    }

    @Test
    fun `returns empty list when catalog is empty`() {
        val result = runBlocking { assistant().recommend(profile, emptyList()) }

        assertTrue(result.isEmpty())
    }
}
