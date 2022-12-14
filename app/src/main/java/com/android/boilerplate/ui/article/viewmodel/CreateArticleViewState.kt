package com.android.boilerplate.ui.article.viewmodel

import com.android.boilerplate.data.model.ErrorsData
import com.android.boilerplate.data.repositories.auth.response.LoginResponse
import com.android.boilerplate.utils.PopupErrorState

sealed class CreateArticleViewState{
    object Loading : CreateArticleViewState()
    data class Success(val message: String = "") : CreateArticleViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : CreateArticleViewState()
    data class InputError(val errorData: ErrorsData? = null) : CreateArticleViewState()
}
