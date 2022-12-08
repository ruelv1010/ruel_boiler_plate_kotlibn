package com.android.boilerplate.di

import com.android.boilerplate.BuildConfig
import com.android.boilerplate.data.repositories.AppRetrofitService
import com.android.boilerplate.data.repositories.auth.AuthRemoteDataSource
import com.android.boilerplate.data.repositories.auth.AuthRepository
import com.android.boilerplate.data.repositories.auth.AuthService
import com.android.boilerplate.security.AuthEncryptedDataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class AuthModule {

    @Provides
    fun providesAuthService(): AuthService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            AuthService::class.java
        )
    }

    @Provides
    fun providesAuthRemoteDataSource(authService: AuthService): AuthRemoteDataSource {
        return AuthRemoteDataSource(authService)
    }

    @Provides
    fun providesAuthRepository(
        authRemoteDataSource: AuthRemoteDataSource,
        encryptedDataManager: AuthEncryptedDataManager
    ): AuthRepository {
        return AuthRepository(authRemoteDataSource, encryptedDataManager)
    }

}