package com.android.boilerplate.di

import com.android.boilerplate.security.AuthEncryptedDataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Provides
    fun providesEncryptedDataManager(): AuthEncryptedDataManager {
        return AuthEncryptedDataManager()
    }
}