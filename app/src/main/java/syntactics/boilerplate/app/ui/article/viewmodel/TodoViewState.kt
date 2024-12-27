package syntactics.boilerplate.app.ui.article.viewmodel

import syntactics.boilerplate.app.data.model.TodoModel
import syntactics.boilerplate.app.data.repositories.article.response.ArticleData
import syntactics.boilerplate.app.utils.PopupErrorState


sealed class TodoViewState{
    object Initial : TodoViewState()
    object Loading : TodoViewState()
    data class Success(val todos: List<TodoModel>) : TodoViewState()
    data class SuccessDelete(val msg:String) : TodoViewState()
    data class Error(val message: String) : TodoViewState()
}
