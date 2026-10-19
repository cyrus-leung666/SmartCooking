package com.example.smartcooking.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcooking.domain.model.BmiResult
import com.example.smartcooking.domain.model.Gender
import com.example.smartcooking.domain.model.UserProfile
import com.example.smartcooking.domain.usecase.CalculateBmiUseCase
import com.example.smartcooking.domain.usecase.GetUserProfileUseCase
import com.example.smartcooking.domain.usecase.SaveUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val gender: Gender = Gender.MALE,
    val age: String = "",
    val heightCm: String = "",
    val weightKg: String = "",
    val activityLevel: Double = 1.2,
    val result: BmiResult? = null,
    val error: String? = null,
    val saved: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val calculateBmiUseCase: CalculateBmiUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getUserProfileUseCase().collect { profile ->
                if (profile != null) {
                    _uiState.update {
                        it.copy(
                            gender = profile.gender,
                            age = profile.age.toString(),
                            heightCm = profile.heightCm.toString(),
                            weightKg = profile.weightKg.toString(),
                            activityLevel = profile.activityLevel
                        )
                    }
                }
            }
        }
    }

    fun onGenderChange(gender: Gender) = _uiState.update { it.copy(gender = gender) }
    fun onAgeChange(value: String) = _uiState.update { it.copy(age = value) }
    fun onHeightChange(value: String) = _uiState.update { it.copy(heightCm = value) }
    fun onWeightChange(value: String) = _uiState.update { it.copy(weightKg = value) }
    fun onActivityChange(value: Double) = _uiState.update { it.copy(activityLevel = value) }

    fun calculate() {
        val state = _uiState.value
        val age = state.age.toIntOrNull()
        val height = state.heightCm.toDoubleOrNull()
        val weight = state.weightKg.toDoubleOrNull()
        if (age == null || height == null || weight == null || age <= 0 || height <= 0 || weight <= 0) {
            _uiState.update { it.copy(error = "请输入有效的年龄、身高和体重") }
            return
        }
        val result = calculateBmiUseCase(state.gender, age, height, weight, state.activityLevel)
        _uiState.update { it.copy(result = result, error = null, saved = false) }
    }

    fun save() {
        val state = _uiState.value
        val age = state.age.toIntOrNull() ?: return
        val height = state.heightCm.toDoubleOrNull() ?: return
        val weight = state.weightKg.toDoubleOrNull() ?: return
        viewModelScope.launch {
            saveUserProfileUseCase(
                UserProfile(state.gender, age, height, weight, state.activityLevel)
            )
            _uiState.update { it.copy(saved = true) }
        }
    }
}
