package com.android.boilerplate.di

import android.app.Application
import androidx.room.Room
import com.android.boilerplate.base.BaseApplication
import com.android.boilerplate.data.local.BoilerPlateDatabase
import com.android.boilerplate.security.AuthEncryptedDataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Provides
    @Singleton
    fun providesArticleAppDatabase(app : Application) : BoilerPlateDatabase {
        return Room.databaseBuilder(
            app,
            BoilerPlateDatabase::class.java,
            "article_db"
        ).fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    fun providesEncryptedDataManager(): AuthEncryptedDataManager {
        return AuthEncryptedDataManager()
    }
}