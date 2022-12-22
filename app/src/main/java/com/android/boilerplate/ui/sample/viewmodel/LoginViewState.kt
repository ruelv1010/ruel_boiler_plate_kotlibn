package com.android.boilerplate.ui.sample.viewmodel

import com.android.boilerplate.data.model.ErrorsData
import com.android.boilerplate.data.repositories.auth.response.LoginResponse
import com.android.boilerplate.utils.PopupErrorState

sealed class LoginViewState{
    object Loading : LoginViewState()
    data class Success(val message: String = "") : LoginViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : LoginViewState()
    data class InputError(val errorData: ErrorsData? = null) : LoginViewState()
}
