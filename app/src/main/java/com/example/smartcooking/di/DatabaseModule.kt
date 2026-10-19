package com.example.smartcooking.di

import android.content.Context
import androidx.room.Room
import com.example.smartcooking.data.local.db.AppDatabase
import com.example.smartcooking.data.local.db.dao.CookingMethodDao
import com.example.smartcooking.data.local.db.dao.IngredientDao
import com.example.smartcooking.data.local.db.dao.RecipeDao
import com.example.smartcooking.data.local.db.dao.RecipeIngredientDao
import com.example.smartcooking.data.local.db.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "smart_cooking.db")
            // TODO: v2 前替换为真实 Migration,避免升级丢数据
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideIngredientDao(db: AppDatabase): IngredientDao = db.ingredientDao()
    @Provides fun provideCookingMethodDao(db: AppDatabase): CookingMethodDao = db.cookingMethodDao()
    @Provides fun provideRecipeDao(db: AppDatabase): RecipeDao = db.recipeDao()
    @Provides fun provideRecipeIngredientDao(db: AppDatabase): RecipeIngredientDao = db.recipeIngredientDao()
}
