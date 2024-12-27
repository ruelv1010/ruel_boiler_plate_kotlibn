package syntactics.boilerplate.app.di


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import syntactics.android.app.BuildConfig
import syntactics.boilerplate.app.data.repositories.AppRetrofitService
import syntactics.boilerplate.app.data.repositories.article.ArticleRemoteDataSource
import syntactics.boilerplate.app.data.repositories.article.ArticleRepository
import syntactics.boilerplate.app.data.repositories.article.ArticleService

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