package com.android.boilerplate.ui.article.viewmodel

import com.android.boilerplate.data.repositories.article.response.ArticleData
import com.android.boilerplate.utils.PopupErrorState

sealed class ArticleDetailViewState{
    object Loading : ArticleDetailViewState()
    data class Success(val articleData: ArticleData) : ArticleDetailViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : ArticleDetailViewState()
}
