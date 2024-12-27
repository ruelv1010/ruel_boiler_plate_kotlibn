package syntactics.boilerplate.app.utils

import android.webkit.MimeTypeMap
import android.text.TextUtils
import android.os.Build
import android.provider.MediaStore
import android.annotation.TargetApi
import android.provider.DocumentsContract
import androidx.annotation.RequiresApi
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import syntactics.boilerplate.app.data.model.TodoModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception

class FirebaseHelper {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val storage = FirebaseStorage.getInstance()

    // Function to add a TodoModel
    fun addTodoItem(todo: TodoModel, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Generate a unique key or use a more robust ID generation
        val todoId = database.child("Todo").push().key
            ?: run {
                // Handle the case where key generation fails
                onFailure(Exception("Failed to generate Todo ID"))
                return
            }

        val todoItem = todo.copy(id = todoId)

        database.child("Todo").child(todoId).setValue(todoItem)
            .addOnSuccessListener {
                Log.d("FirebaseHelper", "Todo item added successfully: $todoId")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseHelper", "Failed to add Todo item", exception)
                onFailure(exception)
            }
    }

    // Function to upload an image to Firebase Storage
    fun uploadImage(imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = storage.reference.child("images/${System.currentTimeMillis()}.jpg")

        // Start upload
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            // Once the file is uploaded, retrieve the download URL
            storageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    onSuccess(uri.toString()) // Return the image URL
                }
                .addOnFailureListener { exception ->
                    onFailure(Exception("Failed to get download URL: ${exception.message}"))
                }
        }.addOnFailureListener { exception ->
            onFailure(Exception("Image upload failed: ${exception.message}"))
        }
    }



    fun fetchAllTodos(callback: (List<TodoModel>) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val todoList = mutableListOf<TodoModel>()
                for (data in snapshot.children) {
                    // Convert the snapshot to TodoModel and set the key as the id
                    val todo = data.getValue(TodoModel::class.java)
                    todo?.let {
                        todoList.add(it.copy(id = data.key))
                    }
                }
                callback(todoList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Log or handle the error
                println("Error fetching data: ${error.message}")
            }
        })
    }
}