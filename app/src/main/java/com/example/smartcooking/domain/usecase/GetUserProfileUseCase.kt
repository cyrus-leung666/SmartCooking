package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<UserProfile?> = userRepository.getProfile()
}
