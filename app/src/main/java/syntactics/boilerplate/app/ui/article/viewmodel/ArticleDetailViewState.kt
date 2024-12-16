package syntactics.boilerplate.app.ui.article.viewmodel

import com.android.app.data.repositories.article.response.ArticleData
import com.android.app.utils.PopupErrorState

sealed class ArticleDetailViewState{
    object Loading : ArticleDetailViewState()
    data class Success(val articleData: ArticleData) : ArticleDetailViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : ArticleDetailViewState()
}
