package com.android.boilerplate.ui.article.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.boilerplate.data.model.ErrorModel
import com.android.boilerplate.data.repositories.article.ArticleRepository
import com.android.boilerplate.data.repositories.article.response.ArticleData
import com.android.boilerplate.data.repositories.article.response.ArticleListResponse
import com.android.boilerplate.data.repositories.auth.AuthRepository
import com.android.boilerplate.utils.PopupErrorState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
) : ViewModel() {

    private val _articleSharedFlow = MutableSharedFlow<ArticleListViewState>()

    val articleSharedFlow: SharedFlow<ArticleListViewState> =
        _articleSharedFlow.asSharedFlow()

    private var currentPage = 0
    private var hasMorePage = true

    fun isFirstPage(): Boolean {
        return currentPage == 1
    }

    fun getArticleList(reset: Boolean = false) {
        if (reset) {
            hasMorePage = true
            currentPage = 1
        } else {
            currentPage++
        }
        if (!hasMorePage) return // block api no more page
        viewModelScope.launch {
            articleRepository.getArticleList(currentPage.toString(), "8")
                .onStart {
                    _articleSharedFlow.emit(ArticleListViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    hasMorePage = it.has_morepages == true
                    _articleSharedFlow.emit(
                        ArticleListViewState.Success(it)
                    )
                }
        }
    }


    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _articleSharedFlow.emit(
                    ArticleListViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _articleSharedFlow.emit(
                    ArticleListViewState.PopupError(
                        PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
                    )
                )
            }
            else -> _articleSharedFlow.emit(
                ArticleListViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }

}