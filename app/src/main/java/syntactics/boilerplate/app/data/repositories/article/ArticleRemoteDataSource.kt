package syntactics.boilerplate.app.data.repositories.article


import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import syntactics.boilerplate.app.data.repositories.article.request.ArticleDetailsRequest
import syntactics.boilerplate.app.data.repositories.article.request.ArticleListRequest
import syntactics.boilerplate.app.data.repositories.article.response.ArticleListResponse
import syntactics.boilerplate.app.data.repositories.article.response.ArticleResponse
import syntactics.boilerplate.app.data.repositories.article.response.GeneralResponse
import syntactics.boilerplate.app.data.repositories.article.response.MyImageResponse
import syntactics.boilerplate.app.utils.asNetWorkRequestBody
import java.io.File
import java.net.HttpURLConnection
import javax.inject.Inject

class ArticleRemoteDataSource @Inject constructor(private val articleService: ArticleService)  {

    suspend fun doCreateArticle(client_id:String,type: String, title: String, description: String, imageFile: File): MyImageResponse {
        val imageFilePart = MultipartBody.Part.createFormData(
            "image",
            imageFile.name,
            imageFile.asNetWorkRequestBody(IMAGE_MIME_TYPE)
        )
        val typePart = RequestBody.create("text/plain".toMediaTypeOrNull(), "image")

        val response = articleService.doCreateArticle(
            imageFilePart,
            typePart,

        )
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getArticleDetails(articleId: Int): ArticleResponse {
        val request = ArticleDetailsRequest(articleId)
        val response = articleService.getArticleDetails(request)

        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }
        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun getArticleList(page: String, perPage: String): ArticleListResponse {
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