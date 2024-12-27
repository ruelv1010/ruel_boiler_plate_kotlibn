package syntactics.boilerplate.app.ui.article.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import syntactics.boilerplate.app.data.model.ErrorModel
import syntactics.boilerplate.app.data.repositories.article.ArticleRepository
import syntactics.boilerplate.app.data.repositories.article.response.ArticleData
import syntactics.boilerplate.app.utils.PopupErrorState
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
) : ViewModel() {

    private val _articleSharedFlow = MutableSharedFlow<ArticleDetailViewState>()

    val articleSharedFlow: SharedFlow<ArticleDetailViewState> =
        _articleSharedFlow.asSharedFlow()


    fun getArticleDetails(articleId: Int) {
        viewModelScope.launch {
            articleRepository.getArticleDetails(articleId)
                .onStart {
                    _articleSharedFlow.emit(ArticleDetailViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _articleSharedFlow.emit(
                        ArticleDetailViewState.Success(it.data?: ArticleData())
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
                    ArticleDetailViewState.PopupError(
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
                    ArticleDetailViewState.PopupError(
                        PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
                    )
                )
            }
            else -> _articleSharedFlow.emit(
                ArticleDetailViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }

}