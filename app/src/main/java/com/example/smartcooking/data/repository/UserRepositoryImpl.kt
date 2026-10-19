package com.example.smartcooking.data.repository

import com.example.smartcooking.data.local.datastore.UserPreferencesDataStore
import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

// 用户档案存 DataStore;users 表保留用于规格书 §4 结构合规,后续接后端时启用
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val dataStore: UserPreferencesDataStore
) : UserRepository {

    override fun getProfile(): Flow<UserProfile?> = dataStore.profileFlow

    override suspend fun saveProfile(profile: UserProfile) = dataStore.saveProfile(profile)
}
