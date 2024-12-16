package syntactics.boilerplate.app.data.repositories.article

import com.android.app.data.repositories.article.request.ArticleDetailsRequest
import com.android.app.data.repositories.article.request.ArticleListRequest
import com.android.app.data.repositories.article.response.ArticleListResponse
import com.android.app.data.repositories.article.response.ArticleResponse
import com.android.app.data.repositories.article.response.GeneralResponse
import com.android.app.utils.asNetWorkRequestBody
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.File
import java.net.HttpURLConnection
import javax.inject.Inject

class ArticleRemoteDataSource @Inject constructor(private val articleService: ArticleService)  {

    suspend fun doCreateArticle(name: String, desc: String, imageFile: File): GeneralResponse{
        val imageFilePart = MultipartBody.Part.createFormData(
            "image",
            imageFile.name,
            imageFile.asNetWorkRequestBody(IMAGE_MIME_TYPE)
        )
        val namePart = MultipartBody.Part.createFormData("name", name)
        val descPart = MultipartBody.Part.createFormData("description", desc)
        val response = articleService.doCreateArticle(
            imageFilePart,
            namePart,
            descPart
        )
        if (response.code() != HttpURLConnection.HTTP_CREATED) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getArticleDetails(articleId: Int): ArticleResponse{
        val request = ArticleDetailsRequest(articleId)
        val response = articleService.getArticleDetails(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getArticleList(page: String, perPage: String): ArticleListResponse{
        val request = ArticleListRequest(perPage, page)
        val response = articleService.getArticleList(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }


    companion object {
        private const val IMAGE_MIME_TYPE = "image/*"
    }

}