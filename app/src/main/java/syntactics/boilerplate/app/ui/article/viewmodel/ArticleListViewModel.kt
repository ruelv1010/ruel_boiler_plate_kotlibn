package syntactics.boilerplate.app.ui.article.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.app.data.model.ErrorModel
import com.android.app.data.repositories.article.ArticleRepository
import com.android.app.utils.AppConstant
import com.android.app.utils.PopupErrorState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
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
                val errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                _articleSharedFlow.emit(
                    ArticleListViewState.PopupError(
                        if (AppConstant.isSessionStatusCode(errorResponse?.status_code.orEmpty())){
                            PopupErrorState.SessionError
                        }else{
                            PopupErrorState.HttpError
                        }
                        , errorResponse?.msg.orEmpty()
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