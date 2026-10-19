package com.example.smartcooking.domain.repository

import com.example.smartcooking.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getProfile(): Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
}
