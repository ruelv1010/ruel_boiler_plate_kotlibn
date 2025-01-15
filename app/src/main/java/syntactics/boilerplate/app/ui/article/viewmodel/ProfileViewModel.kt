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

    val viewState: StateFlow<ProfileViewState> = _viewState.asStateFlow()


    private val database = FirebaseDatabase.getInstance()
    private val todosRef = database.getReference("Users")


    init {
        fetchTodos()
    }


    fun fetchTodos() {
        viewModelScope.launch {
            try {

                _viewState.value = ProfileViewState.Loading


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

                        _viewState.value = ProfileViewState.Success(todoList)
                    }

                    override fun onCancelled(error: DatabaseError) {

                        _viewState.value = ProfileViewState.Error(error.message)
                    }
                })
            } catch (e: Exception) {

                _viewState.value = ProfileViewState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun deleteTodo(todoId: String) {
        todosRef.child(todoId).removeValue()
            .addOnSuccessListener {

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

                _viewState.value = ProfileViewState.Loading


                val snapshot = todosRef.orderByChild("user_id").equalTo(user_id).get().await()

                if (snapshot.exists()) {

                    for (childSnapshot in snapshot.children) {
                        childSnapshot.ref.setValue(updatedTodo).await()
                    }



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

                _viewState.value = ProfileViewState.Loading


                val query = todosRef.orderByChild("user_id").equalTo(user_id)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            val userList = mutableListOf<MyUsersModel>()


                            for (childSnapshot in snapshot.children) {
                                val user = childSnapshot.getValue(MyUsersModel::class.java)
                                user?.let {

                                    userList.add(it)
                                }
                            }


                            if (userList.isNotEmpty()) {
                                _viewState.value = ProfileViewState.SuccessProfile(userList)
                            } else {

                                _viewState.value = ProfileViewState.Error("No user found with this email")
                            }
                        } else {

                            _viewState.value = ProfileViewState.Error("No user found with this email")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                        _viewState.value = ProfileViewState.Error(error.message)
                    }
                })
            } catch (e: Exception) {

                _viewState.value = ProfileViewState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun getDetailsByEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            try {

                _viewState.value = ProfileViewState.Loading


                val query = todosRef
                    .orderByChild("email") // Assuming the field storing email is named "email"
                    .equalTo(email)

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            val userList = mutableListOf<MyUsersModel>()


                            for (childSnapshot in snapshot.children) {
                                val user = childSnapshot.getValue(MyUsersModel::class.java)
                                user?.let {

                                    if (it.password == password) {

                                        userList.add(it)
                                    }
                                }
                            }

                            // If we find any user(s) with matching email and password, update the state with the first match
                            if (userList.isNotEmpty()) {
                                _viewState.value = ProfileViewState.SuccessProfile(userList)
                            } else {

                                _viewState.value = ProfileViewState.Error("No user found with this email and password")
                            }
                        } else {

                            _viewState.value = ProfileViewState.Error("Invalid email or password")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                        _viewState.value = ProfileViewState.Error(error.message)
                    }
                })
            } catch (e: Exception) {

                _viewState.value = ProfileViewState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }


}