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
import syntactics.boilerplate.app.utils.CommonLogger
import syntactics.boilerplate.app.utils.PopupErrorState
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class CreateArticleViewModel @Inject constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {

    private val _articleSharedFlow = MutableSharedFlow<CreateArticleViewState>()

    val articleSharedFlow: SharedFlow<CreateArticleViewState> =
        _articleSharedFlow.asSharedFlow()

    lateinit var imageFile: File


    fun doCreateArticle(type: String, title: String, description: String, imageFile: File) {
        viewModelScope.launch {
            articleRepository.doCreateArticle(type, title,description, imageFile)
                .onStart {
                    _articleSharedFlow.emit(CreateArticleViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)
                    CommonLogger.instance.sysLogE("CreateViewModel", exception.localizedMessage, exception)
                }
                .collect {
                    it.data?.let { it1 ->
                        CreateArticleViewState.Success(it.msg.orEmpty(),
                            it1
                        )
                    }?.let { it2 ->
                        _articleSharedFlow.emit(
                            it2
                        )
                    }
                }
        }
    }


    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _articleSharedFlow.emit(
                    CreateArticleViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                if (errorResponse?.has_requirements == true) {
                    _articleSharedFlow.emit(CreateArticleViewState.InputError(errorResponse.errors))
                } else {
                    _articleSharedFlow.emit(
                        CreateArticleViewState.PopupError(
                            PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
                        )
                    )
                }
            }
            else -> _articleSharedFlow.emit(
                CreateArticleViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }

}