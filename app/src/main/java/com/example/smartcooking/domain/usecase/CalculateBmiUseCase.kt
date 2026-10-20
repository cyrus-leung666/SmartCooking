package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.model.BmiResult
import com.example.smartcooking.domain.model.Gender
import com.example.smartcooking.domain.model.NutritionTarget
import javax.inject.Inject
import kotlin.math.round

class CalculateBmiUseCase @Inject constructor() {

    operator fun invoke(
        gender: Gender,
        age: Int,
        heightCm: Double,
        weightKg: Double,
        activityLevel: Double
    ): BmiResult {
        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)

        val bmr = if (gender == Gender.MALE) {
            10 * weightKg + 6.25 * heightCm - 5 * age + 5
        } else {
            10 * weightKg + 6.25 * heightCm - 5 * age - 161
        }
        val tdee = bmr * activityLevel

        val (category, targetCalories, ratio) = when {
            bmi >= 24.0 -> Triple("Overweight", tdee * 0.8, Triple(0.40, 0.30, 0.30))
            bmi < 18.5 -> Triple("Underweight", tdee * 1.15, Triple(0.55, 0.25, 0.20))
            else -> Triple("Normal", tdee, Triple(0.50, 0.20, 0.30))
        }

        val advice = when (category) {
            "Overweight" -> "Control calorie intake and add aerobic exercise 3-5 times per week to gradually lose fat."
            "Underweight" -> "Increase calorie intake moderately and combine with strength training for healthy weight gain."
            else -> "Maintain your current weight; moderate-intensity resistance training is recommended."
        }

        return BmiResult(
            bmi = round2(bmi),
            category = category,
            bmr = round2(bmr),
            tdee = round2(tdee),
            targetCalories = round2(targetCalories),
            nutrition = NutritionTarget(
                carbsG = round1(targetCalories * ratio.first / 4.0),
                proteinG = round1(targetCalories * ratio.second / 4.0),
                fatG = round1(targetCalories * ratio.third / 9.0),
                ratioLabel = "${(ratio.first * 100).toInt()} : ${(ratio.second * 100).toInt()} : ${(ratio.third * 100).toInt()}",
                dietaryAdvice = advice
            )
        )
    }

    private fun round2(value: Double): Double = round(value * 100.0) / 100.0

    private fun round1(value: Double): Double = round(value * 10.0) / 10.0
}
