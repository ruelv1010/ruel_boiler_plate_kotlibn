package syntactics.boilerplate.app.ui.article.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch
import syntactics.android.app.R
import syntactics.android.app.databinding.ActivityCreateArticleBinding
import syntactics.boilerplate.app.data.model.ErrorsData
import syntactics.boilerplate.app.data.model.TodoModel
import syntactics.boilerplate.app.ui.article.viewmodel.CreateArticleViewModel
import syntactics.boilerplate.app.ui.article.viewmodel.CreateArticleViewState
import syntactics.boilerplate.app.utils.FirebaseHelper
import syntactics.boilerplate.app.utils.loadImage
import syntactics.boilerplate.app.utils.setOnSingleClickListener
import syntactics.boilerplate.app.utils.showPopupError
import java.util.UUID


@AndroidEntryPoint
class CreateArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateArticleBinding
    private val viewModel: CreateArticleViewModel by viewModels()
    private var loadingDialog: syntactics.boilerplate.app.utils.dialog.CommonDialog? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: FirebaseStorage

    private var imageUri: Uri? = null
    private val firebaseHelper = FirebaseHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateArticleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setClickListener()
        observeArticle()

    }

    private fun setClickListener() = binding.run{
        articleImageView.setOnSingleClickListener {
            pickImageFromGallery()
        }
        createArticleButton.setOnSingleClickListener {
            val title = titleEditText.text.toString()
            val description = descEditText.text.toString()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this@CreateArticleActivity, "Title and Description cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                uploadTodo(title, description)
            }
         }
    }
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()
            binding.articleImageView.loadImage(imageUri.toString(),this@CreateArticleActivity)
        }
    }


    private fun uploadTodo(title: String, description: String) {
        if (imageUri != null) {
            firebaseHelper.uploadImage(imageUri!!, { imageUrl ->

            }, { error ->
                Toast.makeText(this, "Image upload failed: ${error.message}", Toast.LENGTH_SHORT).show()
            })
        } else {
            val todo = TodoModel(title = title, description = description)
            saveTodoToFirebase(todo)
        }
    }


    private fun saveTodoToFirebase(todo: TodoModel) {
        firebaseHelper.addTodoItem(todo, {
            Toast.makeText(this, "Todo added successfully", Toast.LENGTH_SHORT).show()
            finish()
        }, { error ->
            Toast.makeText(this, "Failed to add Todo: ${error.message}", Toast.LENGTH_SHORT).show()
        })
    }


    private fun observeArticle(){
        lifecycleScope.launch {
            viewModel.articleSharedFlow.collect{
                handleViewState(it)
            }
        }
    }



    private fun handleViewState(viewState: CreateArticleViewState){
        when(viewState){
            is CreateArticleViewState.Loading -> showLoadingDialog(R.string.create_article_loading)
            is CreateArticleViewState.Success -> {
                hideLoadingDialog()
                finish()
                Toast.makeText(this, viewState.message, Toast.LENGTH_SHORT).show()
            }
            is CreateArticleViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(this, supportFragmentManager, viewState.errorCode, viewState.message)
            }
            is CreateArticleViewState.InputError -> {
                hideLoadingDialog()
                handleInputError(viewState.errorData?: ErrorsData())
            }
        }
    }

    private fun handleInputError(errorsData: ErrorsData){
        if (errorsData.name?.get(0)?.isNotEmpty() == true) binding.titleEditText.error = errorsData.name?.get(0)
        if (errorsData.desc?.get(0)?.isNotEmpty() == true) binding.descEditText.error = errorsData.desc?.get(0)
        if (errorsData.image?.get(0)?.isNotEmpty() == true)
            Toast.makeText(this, errorsData.image?.get(0), Toast.LENGTH_SHORT).show()
    }





    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = syntactics.boilerplate.app.utils.dialog.CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
        }
        loadingDialog?.show(supportFragmentManager)
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoadingDialog()
    }


    companion object {
        const val IMAGE_PICK_CODE = 1000
        fun getIntent(context: Context): Intent {
            return Intent(context, CreateArticleActivity::class.java)
        }
        private const val PERMISSION_WRITE_EXTERNAL = 101
    }





}