package syntactics.boilerplate.app.ui.article.viewmodel


import syntactics.boilerplate.app.data.model.TodoModel
import syntactics.boilerplate.app.data.repositories.article.response.ArticleListResponse
import syntactics.boilerplate.app.utils.PopupErrorState

sealed class ArticleListViewState{
    object Initial : ArticleListViewState()

    object Loading : ArticleListViewState()
    data class Success(val articleListResponse: ArticleListResponse? = ArticleListResponse()) : ArticleListViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : ArticleListViewState()
    data class SuccessTodo(val todos: List<TodoModel>) : ArticleListViewState()
    data class ErrorTodo(val message: String) : ArticleListViewState()

}
