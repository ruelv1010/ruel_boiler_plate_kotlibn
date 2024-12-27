package syntactics.boilerplate.app.ui.article.viewmodel

import syntactics.boilerplate.app.data.model.ErrorsData
import syntactics.boilerplate.app.data.repositories.article.response.MyImageData
import syntactics.boilerplate.app.data.repositories.article.response.MyImageResponse
import syntactics.boilerplate.app.utils.PopupErrorState


sealed class CreateArticleViewState{
    object Loading : CreateArticleViewState()
    data class Success(val message: String = "",val myImageData: MyImageData) : CreateArticleViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : CreateArticleViewState()
    data class InputError(val errorData: ErrorsData? = null) : CreateArticleViewState()
}
