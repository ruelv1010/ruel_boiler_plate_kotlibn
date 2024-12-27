package syntactics.boilerplate.app.di


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import syntactics.android.app.BuildConfig
import syntactics.boilerplate.app.data.local.BoilerPlateDatabase
import syntactics.boilerplate.app.data.local.UserDao
import syntactics.boilerplate.app.data.repositories.AppRetrofitService
import syntactics.boilerplate.app.data.repositories.auth.AuthLocalDataSource
import syntactics.boilerplate.app.data.repositories.auth.AuthRemoteDataSource
import syntactics.boilerplate.app.data.repositories.auth.AuthRepository
import syntactics.boilerplate.app.data.repositories.auth.AuthService
import syntactics.boilerplate.app.security.AuthEncryptedDataManager

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
    fun providesUserDao(db: BoilerPlateDatabase): UserDao {
        return db.userDao
    }

    @Provides
    fun providesAuthRemoteDataSource(authService: AuthService): AuthRemoteDataSource {
        return AuthRemoteDataSource(authService)
    }

    @Provides
    fun providesAuthLocalDataSource(userDao: UserDao): AuthLocalDataSource {
        return AuthLocalDataSource(userDao)
    }

    @Provides
    fun providesAuthRepository(
        authRemoteDataSource: AuthRemoteDataSource,
        encryptedDataManager: AuthEncryptedDataManager,
        authLocalDataSource: AuthLocalDataSource
    ): AuthRepository {
        return AuthRepository(authRemoteDataSource, encryptedDataManager, authLocalDataSource)
    }

}