package syntactics.boilerplate.app.data.repositories.article.response

import syntactics.boilerplate.app.data.repositories.auth.response.AvatarData
import syntactics.boilerplate.app.data.repositories.auth.response.DateCreatedData


data class MyImageResponse(
    val data: MyImageData? = null,
    val msg: String? = null,
    val status: String? = null,

)

data class MyImageData(
    val id: String? = null,
    val link: String? = null,
)