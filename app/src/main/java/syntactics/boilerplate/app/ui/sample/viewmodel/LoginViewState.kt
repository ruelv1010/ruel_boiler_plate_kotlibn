package syntactics.boilerplate.app.ui.sample.viewmodel

import syntactics.boilerplate.app.data.local.UserLocalData
import syntactics.boilerplate.app.data.model.ErrorsData
import syntactics.boilerplate.app.utils.PopupErrorState


sealed class LoginViewState{
    object Loading : LoginViewState()
    data class Success(val message: String = "") : LoginViewState()
    data class SuccessGetUserInfo(val userLocalData: UserLocalData = UserLocalData()) : LoginViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : LoginViewState()
    data class InputError(val errorData: ErrorsData? = null) : LoginViewState()
}
