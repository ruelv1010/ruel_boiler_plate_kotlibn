package syntactics.boilerplate.app.data.repositories.article

import com.android.app.data.repositories.article.response.ArticleListResponse
import com.android.app.data.repositories.article.response.ArticleResponse
import com.android.app.data.repositories.article.response.GeneralResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val articleRemoteDataSource: ArticleRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun doCreateArticle(name: String, desc: String, imageFile: File): Flow<GeneralResponse> {
        return flow {
            val response = articleRemoteDataSource.doCreateArticle(name, desc, imageFile)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getArticleDetails(articleId: Int): Flow<ArticleResponse> {
        return flow {
            val response = articleRemoteDataSource.getArticleDetails(articleId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getArticleList(page: String, perPage:String): Flow<ArticleListResponse> {
        return flow {
            val response = articleRemoteDataSource.getArticleList(page, perPage)
            emit(response)
        }.flowOn(ioDispatcher)
    }

}