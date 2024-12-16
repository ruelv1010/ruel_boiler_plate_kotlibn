package syntactics.boilerplate.app.data.repositories.article.response

data class ArticleListResponse(
    val data: List<ArticleData>? = null,
    val msg: String? = null,
    val status: Boolean? = false,
    val status_code: String? = null,
    val has_morepages: Boolean? = false,
    val total: Int? = 0
)