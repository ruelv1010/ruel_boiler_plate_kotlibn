package syntactics.boilerplate.app.ui.article.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import syntactics.boilerplate.app.data.model.ErrorModel
import syntactics.boilerplate.app.data.model.TodoModel
import syntactics.boilerplate.app.data.repositories.article.ArticleRepository
import syntactics.boilerplate.app.data.repositories.article.response.ArticleData
import syntactics.boilerplate.app.security.AuthEncryptedDataManager
import syntactics.boilerplate.app.utils.PopupErrorState
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val encryptedDataManager: AuthEncryptedDataManager
) : ViewModel() {

    private val _viewState = MutableStateFlow<TodoViewState>(TodoViewState.Initial)
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    // Expose as immutable state flow
    val viewState: StateFlow<TodoViewState> = _viewState.asStateFlow()

    // Firebase Database reference
    private val database = FirebaseDatabase.getInstance()
    private val todosRef = database.getReference("Todo")


    // Initialize ViewModel and fetch todos
    init {
        fetchTodos(encryptedDataManager.getMYID())
    }

    // Fetch todos using coroutines
    fun fetchTodos(userId: String) {
        viewModelScope.launch {
            try {
                // Set loading state
                _viewState.value = TodoViewState.Loading

                // Fetch todos
                val todoList = mutableListOf<TodoModel>()

                todosRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            val todo = childSnapshot.getValue(TodoModel::class.java)
                            todo?.let {
                                val todoWithId = it.copy(id = childSnapshot.key ?: "")
                                if (todoWithId.user_id == userId) { // Filter by userId
                                    todoList.add(todoWithId)
                                }
                            }
                        }

                        // Update state with filtered todos
                        _viewState.value = TodoViewState.Success(todoList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                        _viewState.value = TodoViewState.Error(error.message)
                    }
                })
            } catch (e: Exception) {
                // Catch any unexpected errors
                _viewState.value = TodoViewState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }


    fun deleteTodo(todoId: String) {
        todosRef.child(todoId).removeValue()
            .addOnSuccessListener {
                // Optional: Handle successful deletion
                _viewState.value = TodoViewState.SuccessDelete("Deleted")
                fetchTodos(encryptedDataManager.getMYID())
            }
            .addOnFailureListener {
                _error.value = "Failed to delete todo"
            }
    }
    fun updateTodo(todo: TodoModel) {
        viewModelScope.launch {
            try {
                // Set loading state
                _viewState.value = TodoViewState.Loading

                // Update in Firebase
                todosRef.child(todo.id.toString()).setValue(todo).await()

                // Refresh todos after update
                fetchTodos(encryptedDataManager.getMYID())
            } catch (e: Exception) {
                _viewState.value = TodoViewState.Error(
                    e.localizedMessage ?: "Failed to update todo"
                )
            }
        }

}
}