package com.example.smartcooking.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.smartcooking.domain.model.Gender
import com.example.smartcooking.domain.model.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_profile")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val profileFlow: Flow<UserProfile?> = context.dataStore.data
        .map { prefs ->
            val gender = prefs[KEY_GENDER] ?: return@map null
            val age = prefs[KEY_AGE] ?: return@map null
            val height = prefs[KEY_HEIGHT] ?: return@map null
            val weight = prefs[KEY_WEIGHT] ?: return@map null
            val activity = prefs[KEY_ACTIVITY] ?: return@map null
            UserProfile(
                gender = if (gender == "male") Gender.MALE else Gender.FEMALE,
                age = age,
                heightCm = height,
                weightKg = weight,
                activityLevel = activity
            )
        }
        .catch { emit(null) } // DataStore 读盘 IOException 重试失败后按"无档案"降级,避免崩溃

    suspend fun saveProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[KEY_GENDER] = if (profile.gender == Gender.MALE) "male" else "female"
            prefs[KEY_AGE] = profile.age
            prefs[KEY_HEIGHT] = profile.heightCm
            prefs[KEY_WEIGHT] = profile.weightKg
            prefs[KEY_ACTIVITY] = profile.activityLevel
        }
    }

    private companion object {
        val KEY_GENDER = stringPreferencesKey("gender")
        val KEY_AGE = intPreferencesKey("age")
        val KEY_HEIGHT = doublePreferencesKey("height_cm")
        val KEY_WEIGHT = doublePreferencesKey("weight_kg")
        val KEY_ACTIVITY = doublePreferencesKey("activity_level")
    }
}
