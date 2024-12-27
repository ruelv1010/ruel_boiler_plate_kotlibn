package syntactics.boilerplate.app.data.repositories.article

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import syntactics.boilerplate.app.data.model.TodoModel
import syntactics.boilerplate.app.data.repositories.article.response.ArticleListResponse
import syntactics.boilerplate.app.data.repositories.article.response.ArticleResponse
import syntactics.boilerplate.app.data.repositories.article.response.GeneralResponse
import syntactics.boilerplate.app.data.repositories.article.response.MyImageResponse
import java.io.File
import javax.inject.Inject

class ArticleRepository @Inject constructor(
    private val articleRemoteDataSource: ArticleRemoteDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val database = FirebaseDatabase.getInstance()
    private val todosRef = database.getReference("Todo")

    // Function to fetch all todos
    fun fetchAllTodos(onResult: (List<TodoModel>) -> Unit, onError: (String) -> Unit) {
        todosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val todoList = mutableListOf<TodoModel>()

                // Iterate through all children in the Todo node
                for (childSnapshot in snapshot.children) {
                    // Convert each child to a Todo object
                    val todo = childSnapshot.getValue(TodoModel::class.java)
                    todo?.let {
                        // Ensure the id is set from the snapshot key
                        val todoWithId = it.copy(id = childSnapshot.key ?: "")
                        todoList.add(todoWithId)
                    }
                }

                // Invoke the callback with the list of todos
                onResult(todoList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors during data fetching
                onError(error.message)
            }
        })
    }

    fun doCreateArticle(type: String, title: String, description: String, imageFile: File): Flow<MyImageResponse> {
        return flow {
            val response = articleRemoteDataSource.doCreateArticle(type, title,description,"", imageFile)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getArticleDetails(articleId: Int): Flow<ArticleResponse> {
        return flow {
            val response = articleRemoteDataSource.getArticleDetails(articleId)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun getArticleList(page: String, perPage:String): Flow<ArticleListResponse> {
        return flow {
            val response = articleRemoteDataSource.getArticleList(page, perPage)
            emit(response)
        }.flowOn(ioDispatcher)
    }

}