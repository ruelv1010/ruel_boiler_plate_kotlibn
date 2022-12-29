package com.android.boilerplate.di

import com.android.boilerplate.BuildConfig
import com.android.boilerplate.data.repositories.AppRetrofitService
import com.android.boilerplate.data.repositories.article.ArticleRemoteDataSource
import com.android.boilerplate.data.repositories.article.ArticleRepository
import com.android.boilerplate.data.repositories.article.ArticleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ArticleModule {

    @Provides
    fun providesArticleService(): ArticleService {
        return AppRetrofitService.Builder().build(
            BuildConfig.BASE_URL,
            ArticleService::class.java
        )
    }

    @Provides
    fun providesArticleRemoteDataSource(authService: ArticleService): ArticleRemoteDataSource {
        return ArticleRemoteDataSource(authService)
    }

    @Provides
    fun providesArticleRepository(authRemoteDataSource: ArticleRemoteDataSource): ArticleRepository {
        return ArticleRepository(authRemoteDataSource)
    }

}