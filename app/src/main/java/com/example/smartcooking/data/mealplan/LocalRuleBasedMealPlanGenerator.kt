package com.example.smartcooking.data.mealplan

import com.example.smartcooking.domain.mealplan.DailyMealPlan
import com.example.smartcooking.domain.mealplan.MealPlanGenerator
import com.example.smartcooking.domain.mealplan.PlannedMeal
import com.example.smartcooking.domain.model.MealType
import com.example.smartcooking.domain.model.RecipeSummary
import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.usecase.CalculateBmiUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.random.Random

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

        val combosByMeal = pools.mapIndexed { index, pool -> buildCombos(pool, budgets[index]) }
        if (combosByMeal.any { it.isEmpty() }) return null

        val candidates = mutableListOf<CandidatePlan>()
        for (breakfast in combosByMeal[0]) {
            for (lunch in combosByMeal[1]) {
                for (dinner in combosByMeal[2]) {
                    val items = breakfast.items + lunch.items + dinner.items
                    val ids = items.map { it.recipeId }
                    if (ids.size != ids.toSet().size) continue
                    candidates += CandidatePlan(
                        combos = listOf(breakfast, lunch, dinner),
                        totalCalories = breakfast.calories + lunch.calories + dinner.calories
                    )
                }
            }
        }
        if (candidates.isEmpty()) return null

        val strict = candidates.filter {
            isValid(it, budgets, target, STRICT_MEAL_TOLERANCE, STRICT_DAY_TOLERANCE)
        }
        val relaxed = if (strict.isEmpty()) {
            candidates.filter {
                isValid(it, budgets, target, RELAXED_MEAL_TOLERANCE, RELAXED_DAY_TOLERANCE)
            }
        } else {
            emptyList()
        }
        val fallback = if (strict.isEmpty() && relaxed.isEmpty()) {
            listOfNotNull(candidates.minByOrNull { abs(it.totalCalories - target) })
        } else {
            emptyList()
        }
        val valid = strict.ifEmpty { relaxed }.ifEmpty { fallback }
        if (valid.isEmpty()) return null

        val selected = selectPlan(valid, exclude)
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

    private fun buildCombos(pool: List<RecipeSummary>, budget: Double): List<Combo> {
        val combos = mutableListOf<Combo>()
        for (i in pool.indices) {
            val a = pool[i]
            combos += Combo(listOf(a), a.totalCalories)
            for (j in i + 1 until pool.size) {
                val b = pool[j]
                combos += Combo(listOf(a, b), a.totalCalories + b.totalCalories)
                for (k in j + 1 until pool.size) {
                    val c = pool[k]
                    combos += Combo(
                        listOf(a, b, c),
                        a.totalCalories + b.totalCalories + c.totalCalories
                    )
                }
            }
        }
        return combos
            .sortedBy { abs(it.calories - budget) }
            .take(MAX_COMBOS_PER_MEAL)
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

    private companion object {
        const val BREAKFAST_RATIO = 0.30
        const val LUNCH_RATIO = 0.40
        const val DINNER_RATIO = 0.30
        const val MAX_COMBOS_PER_MEAL = 40
        const val STRICT_MEAL_TOLERANCE = 0.25
        const val STRICT_DAY_TOLERANCE = 0.10
        const val RELAXED_MEAL_TOLERANCE = 0.35
        const val RELAXED_DAY_TOLERANCE = 0.15
    }
}
