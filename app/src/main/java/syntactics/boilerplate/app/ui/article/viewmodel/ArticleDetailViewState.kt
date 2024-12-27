package syntactics.boilerplate.app.ui.article.viewmodel

import syntactics.boilerplate.app.data.repositories.article.response.ArticleData
import syntactics.boilerplate.app.utils.PopupErrorState


sealed class ArticleDetailViewState{
    object Loading : ArticleDetailViewState()
    data class Success(val articleData: ArticleData) : ArticleDetailViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : ArticleDetailViewState()
}
