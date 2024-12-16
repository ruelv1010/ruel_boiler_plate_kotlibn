package syntactics.boilerplate.app.di

import syntactics.boilerplate.app.BuildConfig
import com.android.app.data.repositories.AppRetrofitService
import com.android.app.data.repositories.article.ArticleRemoteDataSource
import com.android.app.data.repositories.article.ArticleRepository
import com.android.app.data.repositories.article.ArticleService
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
            syntactics.boilerplate.app.BuildConfig.BASE_URL,
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