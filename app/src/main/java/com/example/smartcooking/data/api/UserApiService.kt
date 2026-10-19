package com.example.smartcooking.data.api

import com.example.smartcooking.data.model.ProfileRequestDto
import com.example.smartcooking.data.model.ProfileResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

// 规格书 §5.1:POST /api/v1/user/profile(后端留桩,当前未启用)
interface UserApiService {
    @POST("api/v1/user/profile")
    suspend fun postProfile(@Body body: ProfileRequestDto): ProfileResponseDto
}
