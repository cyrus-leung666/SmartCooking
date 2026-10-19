package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(profile: UserProfile) = userRepository.saveProfile(profile)
}
