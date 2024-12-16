package syntactics.boilerplate.app.data.repositories.article.response

import com.android.app.data.repositories.auth.response.AvatarData
import com.android.app.data.repositories.auth.response.DateCreatedData

data class ArticleResponse(
    val data: ArticleData? = null,
    val msg: String? = null,
    val status: Boolean? = null,
    val status_code: String? = null
)

data class ArticleData(
    val article_id: Int? = null,
    val date_created: DateCreatedData? = null,
    val description: String? = null,
    val image: AvatarData? = null,
    val name: String? = null
)