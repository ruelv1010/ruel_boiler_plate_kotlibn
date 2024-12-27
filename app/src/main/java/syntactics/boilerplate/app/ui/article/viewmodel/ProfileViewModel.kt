package syntactics.boilerplate.app.ui.article.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import syntactics.boilerplate.app.data.model.MyUsersModel
import syntactics.boilerplate.app.data.repositories.article.ArticleRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow<ProfileViewState>(ProfileViewState.Initial)
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    // Expose as immutable state flow
    val viewState: StateFlow<ProfileViewState> = _viewState.asStateFlow()

    // Firebase Database reference
    private val database = FirebaseDatabase.getInstance()
    private val todosRef = database.getReference("Users")

    // Initialize ViewModel and fetch todos
    init {
        fetchTodos()
    }

    // Fetch todos using coroutines
    fun fetchTodos() {
        viewModelScope.launch {
            try {
                // Set loading state
                _viewState.value = ProfileViewState.Loading

                // Fetch todos
                val todoList = mutableListOf<MyUsersModel>()

                todosRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children) {
                            val todo = childSnapshot.getValue(MyUsersModel::class.java)
                            todo?.let {
                                val todoWithId = it.copy(id = childSnapshot.key ?: "")
                                todoList.add(todoWithId)
                            }
                        }

                        // Update state with todos
                        _viewState.value = ProfileViewState.Success(todoList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                        _viewState.value = ProfileViewState.Error(error.message)
                    }
                })
            } catch (e: Exception) {
                // Catch any unexpected errors
                _viewState.value = ProfileViewState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun deleteTodo(todoId: String) {
        todosRef.child(todoId).removeValue()
            .addOnSuccessListener {
                // Optional: Handle successful deletion
                _viewState.value = ProfileViewState.SuccessDelete("Deleted")
                fetchTodos()
            }
            .addOnFailureListener {
                _error.value = "Failed to delete todo"
            }
    }
    fun updateTodoByEmail(user_id: String, updatedTodo: MyUsersModel) {
        viewModelScope.launch {
            try {
                // Set loading state
                _viewState.value = ProfileViewState.Loading

                // Query the database for the matching email
                val snapshot = todosRef.orderByChild("user_id").equalTo(user_id).get().await()

                if (snapshot.exists()) {
                    // Loop through matching records and update them
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.ref.setValue(updatedTodo).await()
                    }

                    // Refresh todos after update
                    fetchTodos()
                } else {
                    _viewState.value = ProfileViewState.Error("No record found with email: $user_id")
                }
            } catch (e: Exception) {
                _viewState.value = ProfileViewState.Error(
                    e.localizedMessage ?: "Failed to update todo"
                )
            }
        }
    }

    fun getDetailsByEmail(user_id: String) {
        viewModelScope.launch {
            try {
                // Set loading state
                _viewState.value = ProfileViewState.Loading

                // Query the database for users with the specified email
                val query = todosRef.orderByChild("user_id").equalTo(user_id)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // List to hold the user(s)
                            val userList = mutableListOf<MyUsersModel>()

                            // Loop through matching records
                            for (childSnapshot in snapshot.children) {
                                val user = childSnapshot.getValue(MyUsersModel::class.java)
                                user?.let {
                                    // Add user to the list (you can assume there's only one user matching the email)
                                    userList.add(it)
                                }
                            }

                            // If we find any user(s), update the state with the first match
                            if (userList.isNotEmpty()) {
                                _viewState.value = ProfileViewState.SuccessProfile(userList)
                            } else {
                                // If no user is found with the given email
                                _viewState.value = ProfileViewState.Error("No user found with this email")
                            }
                        } else {
                            // Handle the case when no records exist for the query
                            _viewState.value = ProfileViewState.Error("No user found with this email")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error while fetching data
                        _viewState.value = ProfileViewState.Error(error.message)
                    }
                })
            } catch (e: Exception) {
                // Catch any unexpected errors
                _viewState.value = ProfileViewState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }


}