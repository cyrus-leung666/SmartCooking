package com.example.smartcooking.domain.usecase

import com.example.smartcooking.domain.seed.SeedStatus
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveSeedCompletionUseCase @Inject constructor(
    private val seedStatus: SeedStatus
) {
    operator fun invoke(): StateFlow<Boolean> = seedStatus.seedCompleted
}
