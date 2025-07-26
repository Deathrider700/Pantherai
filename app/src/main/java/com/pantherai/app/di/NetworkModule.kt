package com.pantherai.app.di

import com.pantherai.app.data.remote.A4FService
import com.pantherai.app.data.remote.SupabaseService
import com.pantherai.app.data.repository.AIRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideA4FService(okHttpClient: OkHttpClient): A4FService {
        return Retrofit.Builder()
            .baseUrl("https://api.a4f.co/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(A4FService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideSupabaseService(): SupabaseService {
        return SupabaseService()
    }
    
    @Provides
    @Singleton
    fun provideAIRepository(
        a4FService: A4FService,
        supabaseService: SupabaseService
    ): AIRepository {
        return AIRepository(a4FService, supabaseService)
    }
} 