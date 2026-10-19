package com.example.smartcooking.domain.seed

import kotlinx.coroutines.flow.StateFlow

interface SeedStatus {
    val seedCompleted: StateFlow<Boolean>
}
