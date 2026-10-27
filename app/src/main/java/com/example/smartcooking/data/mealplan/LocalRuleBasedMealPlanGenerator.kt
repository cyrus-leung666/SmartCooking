package com.example.smartcooking.data.mealplan

import com.example.smartcooking.domain.mealplan.DailyMealPlan
import com.example.smartcooking.domain.mealplan.MealPlanGenerator
import com.example.smartcooking.domain.mealplan.PlannedMeal
import com.example.smartcooking.domain.model.MealType
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.usecase.CalculateBmiUseCase
import java.util.PriorityQueue
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.random.Random

/**
 * 本地三餐规划器。组合搜索是 CPU 密集型工作,调用方应在 Dispatchers.Default 执行。
 * 每餐先保留最接近预算的 [MAX_COMBOS_PER_MEAL] 组;若严格/放宽都无解,逐步扩大上限重试。
 * 菜谱池超过 [MAX_POOL_SIZE_PER_MEAL] 时按单品热量接近度预筛,控制组合规模。
 */
@Singleton
class LocalRuleBasedMealPlanGenerator @Inject constructor(
    private val calculateBmiUseCase: CalculateBmiUseCase,
    private val random: Random
) : MealPlanGenerator {

    override fun generate(
        profile: UserProfile,
        recipes: List<RecipeSummary>,
        exclude: Set<String>
    ): DailyMealPlan? {
        if (recipes.isEmpty()) return null

        val target = calculateBmiUseCase(
            profile.gender,
            profile.age,
            profile.heightCm,
            profile.weightKg,
            profile.activityLevel
        ).targetCalories
        if (!(target > 0.0) || target.isNaN() || target.isInfinite()) return null

        val mealTypes = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER)
        val budgets = listOf(
            target * BREAKFAST_RATIO,
            target * LUNCH_RATIO,
            target * DINNER_RATIO
        )
        val pools = mealTypes.map { type -> recipes.filter { type in it.mealTypes } }
        if (pools.any { it.isEmpty() }) return null

        var validPlans: List<CandidatePlan>? = null
        var bestFallback: CandidatePlan? = null
        for (limit in COMBO_LIMITS) {
            val combosByMeal = pools.mapIndexed { index, pool ->
                buildCombos(boundedPool(pool, budgets[index]), budgets[index], limit)
            }
            if (combosByMeal.any { it.isEmpty() }) continue

            val result = searchPlans(combosByMeal, budgets, target)
            if (result.strict.isNotEmpty()) {
                validPlans = result.strict
                break
            }
            if (result.relaxed.isNotEmpty()) {
                validPlans = result.relaxed
                break
            }
            bestFallback = result.bestFallback
        }

        val candidates = validPlans ?: listOfNotNull(bestFallback).ifEmpty { return null }
        val selected = selectPlan(candidates, exclude)
        return DailyMealPlan(
            targetCalories = target,
            meals = mealTypes.mapIndexed { index, type ->
                PlannedMeal(
                    type = type,
                    targetCalories = budgets[index],
                    items = selected.combos[index].items
                )
            }
        )
    }

    /** 池过大时按单品热量与餐预算的接近度预筛,避免 O(n³) 枚举失控。 */
    private fun boundedPool(pool: List<RecipeSummary>, budget: Double): List<RecipeSummary> =
        if (pool.size <= MAX_POOL_SIZE_PER_MEAL) {
            pool
        } else {
            pool.sortedBy { abs(it.totalCalories - budget) }.take(MAX_POOL_SIZE_PER_MEAL)
        }

    /** 用大小为 limit 的最大堆保留最接近预算的 1-3 样组合。 */
    private fun buildCombos(pool: List<RecipeSummary>, budget: Double, limit: Int): List<Combo> {
        val best = PriorityQueue<Combo>(compareByDescending { abs(it.calories - budget) })

        fun consider(combo: Combo) {
            val deviation = abs(combo.calories - budget)
            if (best.size < limit) {
                best.add(combo)
            } else {
                val worst = best.peek()
                if (worst != null && deviation < abs(worst.calories - budget)) {
                    best.poll()
                    best.add(combo)
                }
            }
        }

        for (i in pool.indices) {
            val a = pool[i]
            consider(Combo(listOf(a), a.totalCalories))
            for (j in i + 1 until pool.size) {
                val b = pool[j]
                consider(Combo(listOf(a, b), a.totalCalories + b.totalCalories))
                for (k in j + 1 until pool.size) {
                    val c = pool[k]
                    consider(
                        Combo(
                            listOf(a, b, c),
                            a.totalCalories + b.totalCalories + c.totalCalories
                        )
                    )
                }
            }
        }

        return best.sortedWith(
            compareBy<Combo> { abs(it.calories - budget) }
                .thenBy { combo -> combo.items.joinToString(",") { it.recipeId } }
        )
    }

    /**
     * 流式搜索三餐组合:只保留严格/放宽合格的组合,以及全天偏差最小的兜底组合,
     * 避免物化 limit³ 个候选对象。
     */
    private fun searchPlans(
        combosByMeal: List<List<Combo>>,
        budgets: List<Double>,
        target: Double
    ): PlanSearchResult {
        val strict = mutableListOf<CandidatePlan>()
        val relaxed = mutableListOf<CandidatePlan>()
        var bestFallback: CandidatePlan? = null
        var bestFallbackDeviation = Double.MAX_VALUE

        for (breakfast in combosByMeal[0]) {
            for (lunch in combosByMeal[1]) {
                for (dinner in combosByMeal[2]) {
                    val items = breakfast.items + lunch.items + dinner.items
                    val ids = items.map { it.recipeId }
                    if (ids.size != ids.toSet().size) continue

                    val candidate = CandidatePlan(
                        combos = listOf(breakfast, lunch, dinner),
                        totalCalories = breakfast.calories + lunch.calories + dinner.calories
                    )
                    when {
                        isValid(candidate, budgets, target, STRICT_MEAL_TOLERANCE, STRICT_DAY_TOLERANCE) ->
                            strict += candidate
                        isValid(candidate, budgets, target, RELAXED_MEAL_TOLERANCE, RELAXED_DAY_TOLERANCE) ->
                            relaxed += candidate
                    }

                    val deviation = abs(candidate.totalCalories - target)
                    if (deviation < bestFallbackDeviation) {
                        bestFallback = candidate
                        bestFallbackDeviation = deviation
                    }
                }
            }
        }

        return PlanSearchResult(strict, relaxed, bestFallback)
    }

    private fun isValid(
        plan: CandidatePlan,
        budgets: List<Double>,
        target: Double,
        mealTolerance: Double,
        dayTolerance: Double
    ): Boolean {
        if (abs(plan.totalCalories - target) / target > dayTolerance) return false
        return plan.combos.indices.all { index ->
            abs(plan.combos[index].calories - budgets[index]) / budgets[index] <= mealTolerance
        }
    }

    private fun selectPlan(plans: List<CandidatePlan>, exclude: Set<String>): CandidatePlan {
        val grouped = plans.groupBy { plan ->
            plan.combos.sumOf { combo -> combo.items.count { it.recipeId in exclude } }
        }
        val minOverlap = grouped.keys.minOrNull() ?: 0
        return grouped[minOverlap].orEmpty().ifEmpty { plans }.random(random)
    }

    private data class Combo(
        val items: List<RecipeSummary>,
        val calories: Double
    )

    private data class CandidatePlan(
        val combos: List<Combo>,
        val totalCalories: Double
    )

    private data class PlanSearchResult(
        val strict: List<CandidatePlan>,
        val relaxed: List<CandidatePlan>,
        val bestFallback: CandidatePlan?
    )

    private companion object {
        const val BREAKFAST_RATIO = 0.30
        const val LUNCH_RATIO = 0.40
        const val DINNER_RATIO = 0.30
        const val MAX_COMBOS_PER_MEAL = 40
        const val MAX_POOL_SIZE_PER_MEAL = 60
        const val STRICT_MEAL_TOLERANCE = 0.25
        const val STRICT_DAY_TOLERANCE = 0.10
        const val RELAXED_MEAL_TOLERANCE = 0.35
        const val RELAXED_DAY_TOLERANCE = 0.15
        val COMBO_LIMITS = intArrayOf(MAX_COMBOS_PER_MEAL, 80, 160)
    }
}
