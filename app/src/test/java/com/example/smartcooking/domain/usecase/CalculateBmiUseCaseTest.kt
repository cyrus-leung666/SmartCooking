package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.model.Gender
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateBmiUseCaseTest {

    private val useCase = CalculateBmiUseCase()

    @Test
    fun `male 24y 175cm 70kg activity 1_375 matches spec example with corrected bmr`() {
        val result = useCase(Gender.MALE, 24, 175.0, 70.0, 1.375)
        assertEquals(22.86, result.bmi, 0.001)
        assertEquals("Normal", result.category)
        assertEquals(1678.75, result.bmr, 0.001)
        assertEquals(2308.28, result.tdee, 0.001)
        assertEquals(2308.28, result.targetCalories, 0.001)
        assertEquals("50 : 20 : 30", result.nutrition.ratioLabel)
        assertEquals(288.5, result.nutrition.carbsG, 0.05)
        assertEquals(115.4, result.nutrition.proteinG, 0.05)
        assertEquals(76.9, result.nutrition.fatG, 0.05)
    }

    @Test
    fun `female formula uses minus 161 constant`() {
        val result = useCase(Gender.FEMALE, 30, 165.0, 55.0, 1.2)
        assertEquals(20.2, result.bmi, 0.001)
        assertEquals(1270.25, result.bmr, 0.001)
        assertEquals(1524.3, result.tdee, 0.001)
        assertEquals(1524.3, result.targetCalories, 0.001)
    }

    @Test
    fun `bmi at least 24 uses cut ratio 40_30_30`() {
        val result = useCase(Gender.MALE, 30, 170.0, 85.0, 1.2)
        assertEquals(29.41, result.bmi, 0.001)
        assertEquals("Overweight", result.category)
        assertEquals(1696.8, result.targetCalories, 0.001)
        assertEquals("40 : 30 : 30", result.nutrition.ratioLabel)
        assertEquals(169.7, result.nutrition.carbsG, 0.05)
        assertEquals(127.3, result.nutrition.proteinG, 0.05)
        assertEquals(56.6, result.nutrition.fatG, 0.05)
    }

    @Test
    fun `bmi below 18_5 uses bulk ratio 55_25_20`() {
        val result = useCase(Gender.FEMALE, 20, 160.0, 45.0, 1.375)
        assertEquals(17.58, result.bmi, 0.001)
        assertEquals("Underweight", result.category)
        assertEquals(1880.11, result.targetCalories, 0.001)
        assertEquals("55 : 25 : 20", result.nutrition.ratioLabel)
        assertEquals(258.5, result.nutrition.carbsG, 0.05)
        assertEquals(117.5, result.nutrition.proteinG, 0.05)
        assertEquals(41.8, result.nutrition.fatG, 0.05)
    }
}
