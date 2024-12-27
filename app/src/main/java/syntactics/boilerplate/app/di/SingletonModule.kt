package syntactics.boilerplate.app.di

import android.app.Application
import androidx.room.Room

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import syntactics.boilerplate.app.data.local.BoilerPlateDatabase
import syntactics.boilerplate.app.security.AuthEncryptedDataManager
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