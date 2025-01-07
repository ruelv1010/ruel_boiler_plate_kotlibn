package syntactics.boilerplate.app.ui.sample.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import syntactics.android.app.R
import syntactics.android.app.databinding.FragmentCreateBinding
import syntactics.android.app.databinding.FragmentHomeBinding
import syntactics.boilerplate.app.data.model.ErrorsData
import syntactics.boilerplate.app.data.model.TodoModel
import syntactics.boilerplate.app.security.AuthEncryptedDataManager
import syntactics.boilerplate.app.ui.article.activity.CreateArticleActivity.Companion.IMAGE_PICK_CODE
import syntactics.boilerplate.app.ui.article.viewmodel.CreateArticleViewModel
import syntactics.boilerplate.app.ui.article.viewmodel.CreateArticleViewState
import syntactics.boilerplate.app.ui.sample.activity.MainActivity
import syntactics.boilerplate.app.ui.sample.viewmodel.LoginViewModel
import syntactics.boilerplate.app.ui.sample.viewmodel.LoginViewState
import syntactics.boilerplate.app.utils.FirebaseHelper
import syntactics.boilerplate.app.utils.dialog.CommonDialog
import syntactics.boilerplate.app.utils.loadImage
import syntactics.boilerplate.app.utils.setOnSingleClickListener
import syntactics.boilerplate.app.utils.showPopupError

@AndroidEntryPoint
class CreateFragment: Fragment() {
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateArticleViewModel by viewModels()
    private var loadingDialog: CommonDialog? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: FirebaseStorage
    private lateinit var encryptedDataManager: AuthEncryptedDataManager
    private var imageUri: Uri? = null
    private val firebaseHelper = FirebaseHelper()
    private val activity by lazy { requireActivity() as MainActivity }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreateBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()

        encryptedDataManager = AuthEncryptedDataManager()
    }


    private fun setClickListeners() = binding.run {
        articleImageView.setOnSingleClickListener {
            pickImageFromGallery()
        }
        createArticleButton.setOnSingleClickListener {
            val title = titleEditText.text.toString()
            val description = descEditText.text.toString()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireActivity(), "Title and Description cannot be empty", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireActivity(), "Image selected", Toast.LENGTH_SHORT).show()
            binding.articleImageView.loadImage(imageUri.toString(),requireActivity())
        }
    }


    private fun uploadTodo(title: String, description: String) {
        if (imageUri != null) {
            firebaseHelper.uploadImage(imageUri!!, { imageUrl ->

                //   val todo = TodoModel(title = title, description = description, imageUrl = imageUrl)
                //  saveTodoToFirebase(todo)
            }, { error ->
                Toast.makeText(requireActivity(), "Image upload failed: ${error.message}", Toast.LENGTH_SHORT).show()
            })
        } else {
            val todo = TodoModel(title = title, description = description, user_id = encryptedDataManager.getMYID())
            saveTodoToFirebase(todo)
        }
    }

    // Function to save the TodoModel to Firebase Database
    private fun saveTodoToFirebase(todo: TodoModel) {
        firebaseHelper.addTodoItem(todo, {
            Toast.makeText(requireActivity(), "Todo added successfully", Toast.LENGTH_SHORT).show()
        }, { error ->
            Toast.makeText(requireActivity(), "Failed to add Todo: ${error.message}", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireActivity(), viewState.message, Toast.LENGTH_SHORT).show()
            }
            is CreateArticleViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
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
            Toast.makeText(requireActivity(), errorsData.image?.get(0), Toast.LENGTH_SHORT).show()
    }





    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
        }
        loadingDialog?.show(childFragmentManager)
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}