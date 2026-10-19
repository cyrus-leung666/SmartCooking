package com.example.smartcooking

import android.app.Application
import android.util.Log
import com.example.smartcooking.data.local.db.seed.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SmartCookingApplication : Application() {

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            try {
                databaseSeeder.seedIfNeeded()
            } catch (e: Exception) {
                Log.w("SmartCooking", "种子数据写入失败", e)
            }
        }
    }
}
