/*package com.example.freshguard.di

import android.content.Context
import com.example.freshguard.data.local.FoodDao
import com.example.freshguard.data.local.FoodDatabase
import com.example.freshguard.data.remote.FirebaseDataSource
import com.example.freshguard.data.repository.FoodRepository
import com.example.freshguard.data.repository.SensorDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFoodDatabase(@ApplicationContext context: Context): FoodDatabase {
        return FoodDatabase.getDatabase(context)
    }

    @Provides
    fun provideFoodDao(database: FoodDatabase): FoodDao {
        return database.foodDao()
    }

    @Provides
    @Singleton
    fun provideFoodRepository(foodDao: FoodDao): FoodRepository {
        return FoodRepository(foodDao)
    }

    @Provides
    @Singleton
    fun provideSensorDataRepository(firebaseDataSource: FirebaseDataSource): SensorDataRepository {
        return SensorDataRepository(firebaseDataSource)
    }
}*/