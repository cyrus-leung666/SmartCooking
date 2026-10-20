package com.example.smartcooking.di

import com.example.smartcooking.data.ai.LocalRuleBasedAiAssistant
import com.example.smartcooking.data.local.db.seed.DatabaseSeeder
import com.example.smartcooking.data.repository.RecipeRepositoryImpl
import com.example.smartcooking.data.repository.UserRepositoryImpl
import com.example.smartcooking.domain.ai.AiRecipeAssistant
import com.example.smartcooking.domain.repository.RecipeRepository
import com.example.smartcooking.domain.repository.UserRepository
import com.example.smartcooking.domain.seed.SeedStatus
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Provides
import kotlin.random.Random

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(impl: RecipeRepositoryImpl): RecipeRepository

    @Binds
    @Singleton
    abstract fun bindAiRecipeAssistant(impl: LocalRuleBasedAiAssistant): AiRecipeAssistant

    // Task 4 审查修正:seed 完成信号,供 RecipeListViewModel 消除首启竞态
    @Binds
    @Singleton
    abstract fun bindSeedStatus(impl: DatabaseSeeder): SeedStatus

    companion object {
        // 随机源注入:生产用默认随机;单测直接以固定种子构造 LocalRuleBasedAiAssistant
        @Provides
        @Singleton
        fun provideRandom(): Random = Random.Default
    }
}
