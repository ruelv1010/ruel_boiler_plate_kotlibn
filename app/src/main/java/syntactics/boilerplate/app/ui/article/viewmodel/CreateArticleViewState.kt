package syntactics.boilerplate.app.ui.article.viewmodel

import com.android.app.data.model.ErrorsData
import com.android.app.utils.PopupErrorState

sealed class CreateArticleViewState{
    object Loading : CreateArticleViewState()
    data class Success(val message: String = "") : CreateArticleViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : CreateArticleViewState()
    data class InputError(val errorData: ErrorsData? = null) : CreateArticleViewState()
}
