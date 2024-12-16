package syntactics.boilerplate.app.data.repositories.article

import com.android.app.data.repositories.article.request.ArticleDetailsRequest
import com.android.app.data.repositories.article.request.ArticleListRequest
import com.android.app.data.repositories.article.response.ArticleListResponse
import com.android.app.data.repositories.article.response.ArticleResponse
import com.android.app.data.repositories.article.response.GeneralResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ArticleService {

    @Multipart
    @POST("api/article/create.json")
    suspend fun doCreateArticle(
        @Part imagePart: MultipartBody.Part,
        @Part namePart: MultipartBody.Part,
        @Part descPart: MultipartBody.Part
    ): Response<GeneralResponse>

    @POST("api/article/show.json")
    suspend fun getArticleDetails(@Body articleDetailsRequest: ArticleDetailsRequest): Response<ArticleResponse>

    @POST("api/article/all.json")
    suspend fun getArticleList(@Body articleListRequest: ArticleListRequest): Response<ArticleListResponse>

}