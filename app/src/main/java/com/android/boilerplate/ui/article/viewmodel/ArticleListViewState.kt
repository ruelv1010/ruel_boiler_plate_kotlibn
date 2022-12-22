package com.android.boilerplate.ui.article.viewmodel

import com.android.boilerplate.data.repositories.article.response.ArticleData
import com.android.boilerplate.data.repositories.article.response.ArticleListResponse
import com.android.boilerplate.utils.PopupErrorState

sealed class ArticleListViewState{
    object Loading : ArticleListViewState()
    data class Success(val articleListResponse: ArticleListResponse? = ArticleListResponse()) : ArticleListViewState()
    data class PopupError(val errorCode: PopupErrorState, val message: String = "") : ArticleListViewState()
}
