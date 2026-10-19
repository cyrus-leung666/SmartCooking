package com.example.smartcooking.data.api

import com.example.smartcooking.data.model.CalorieRequestDto
import com.example.smartcooking.data.model.CalorieResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

// 规格书 §5.2:POST /api/v1/calculator/recipe-calorie(后端留桩,当前未启用)
interface CalorieApiService {
    @POST("api/v1/calculator/recipe-calorie")
    suspend fun calculateRecipeCalorie(@Body body: CalorieRequestDto): CalorieResponseDto
}
