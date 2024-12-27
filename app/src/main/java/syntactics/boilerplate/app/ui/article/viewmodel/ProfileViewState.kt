package syntactics.boilerplate.app.ui.article.viewmodel

import syntactics.boilerplate.app.data.model.MyUsersModel


sealed class ProfileViewState{
    object Initial : ProfileViewState()
    object Loading : ProfileViewState()
    data class Success(val todos: List<MyUsersModel>) : ProfileViewState()

    data class SuccessProfile(val todos: MutableList<MyUsersModel>) : ProfileViewState()
    data class SuccessDelete(val msg:String) : ProfileViewState()
    data class Error(val message: String) : ProfileViewState()
}
