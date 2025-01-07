package syntactics.boilerplate.app.data.repositories.article


import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import syntactics.boilerplate.app.data.repositories.article.request.ArticleDetailsRequest
import syntactics.boilerplate.app.data.repositories.article.request.ArticleListRequest
import syntactics.boilerplate.app.data.repositories.article.response.ArticleListResponse
import syntactics.boilerplate.app.data.repositories.article.response.ArticleResponse
import syntactics.boilerplate.app.data.repositories.article.response.GeneralResponse
import syntactics.boilerplate.app.data.repositories.article.response.MyImageResponse

interface ArticleService {

    @Multipart
    @POST("image")
    @Headers(
     //   "User-Agent: curl/7.84.0",
        "Authorization: Client-ID a85319fa1f17f5c"  // Fixed: Authorization as a header
    )
    suspend fun doCreateArticle(
        @Part image: MultipartBody.Part,
        @Part("type") type: RequestBody,
    ): Response<MyImageResponse>





    @POST("api/article/show.json")
    suspend fun getArticleDetails(@Body articleDetailsRequest: ArticleDetailsRequest): Response<ArticleResponse>

    @POST("api/article/all.json")
    suspend fun getArticleList(@Body articleListRequest: ArticleListRequest): Response<ArticleListResponse>

}