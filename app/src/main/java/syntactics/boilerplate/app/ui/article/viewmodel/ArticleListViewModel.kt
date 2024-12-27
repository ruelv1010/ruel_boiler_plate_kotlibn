package syntactics.boilerplate.app.ui.article.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import syntactics.boilerplate.app.data.model.ErrorModel
import syntactics.boilerplate.app.data.model.TodoModel
import syntactics.boilerplate.app.data.repositories.article.ArticleRepository
import syntactics.boilerplate.app.utils.AppConstant
import syntactics.boilerplate.app.utils.PopupErrorState
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

    private val _viewState = MutableStateFlow<ArticleListViewState>(ArticleListViewState.Initial)

    // Expose as immutable state flow
    val viewState: StateFlow<ArticleListViewState> = _viewState.asStateFlow()


    private val database = FirebaseDatabase.getInstance()
    private val todosRef = database.getReference("Todo")

    // Initialize ViewModel and fetch todos
    init {
        fetchTodos()
    }

    // Fetch todos using coroutines
    fun fetchTodos() {
        viewModelScope.launch {
            try {
                // Set loading state
                _viewState.value = ArticleListViewState.Loading

                // Fetch todos
                val todoList = mutableListOf<TodoModel>()

                todosRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            val todo = childSnapshot.getValue(TodoModel::class.java)
                            todo?.let {
                                val todoWithId = it.copy(id = childSnapshot.key ?: "")
                                todoList.add(todoWithId)
                            }
                        }

                        // Update state with todos
                        _viewState.value = ArticleListViewState.SuccessTodo(todoList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                        _viewState.value = ArticleListViewState.ErrorTodo(error.message)
                    }
                })
            } catch (e: Exception) {
                // Catch any unexpected errors
                _viewState.value = ArticleListViewState.ErrorTodo(e.localizedMessage ?: "Unknown error")
            }
        }
    }

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