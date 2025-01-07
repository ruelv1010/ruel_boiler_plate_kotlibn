package syntactics.boilerplate.app.ui.sample.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import syntactics.android.app.R
import syntactics.android.app.databinding.FragmentCreateBinding
import syntactics.android.app.databinding.FragmentHomeBinding
import syntactics.android.app.databinding.FragmentProfileBinding
import syntactics.boilerplate.app.data.model.ErrorsData
import syntactics.boilerplate.app.data.model.MyUsersModel
import syntactics.boilerplate.app.data.model.TodoModel
import syntactics.boilerplate.app.security.AuthEncryptedDataManager
import syntactics.boilerplate.app.ui.article.activity.CreateArticleActivity.Companion.IMAGE_PICK_CODE
import syntactics.boilerplate.app.ui.article.viewmodel.CreateArticleViewModel
import syntactics.boilerplate.app.ui.article.viewmodel.CreateArticleViewState
import syntactics.boilerplate.app.ui.article.viewmodel.ProfileViewModel
import syntactics.boilerplate.app.ui.article.viewmodel.ProfileViewState
import syntactics.boilerplate.app.ui.article.viewmodel.TodoViewModel
import syntactics.boilerplate.app.ui.article.viewmodel.TodoViewState
import syntactics.boilerplate.app.ui.sample.activity.MainActivity
import syntactics.boilerplate.app.ui.sample.viewmodel.LoginViewModel
import syntactics.boilerplate.app.ui.sample.viewmodel.LoginViewState
import syntactics.boilerplate.app.utils.FirebaseHelper
import syntactics.boilerplate.app.utils.dialog.CommonDialog
import syntactics.boilerplate.app.utils.getFileFromUri
import syntactics.boilerplate.app.utils.loadImage
import syntactics.boilerplate.app.utils.setOnSingleClickListener
import syntactics.boilerplate.app.utils.showPopupError

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateArticleViewModel by viewModels()
    private var loadingDialog: CommonDialog? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var storageRef: FirebaseStorage
    private lateinit var encryptedDataManager: AuthEncryptedDataManager
    private var imageUri: Uri? = null
    private val firebaseHelper = FirebaseHelper()
    private val activity by lazy { requireActivity() as MainActivity }
    private val profileViewModel: ProfileViewModel by viewModels()
    private var myImage: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        observeArticle()
        encryptedDataManager = AuthEncryptedDataManager()
        observeViewModel()

    }

    override fun onResume() {
        super.onResume()
          profileViewModel.getDetailsByEmail(encryptedDataManager.getMYID())
    }

    private fun setClickListeners() = binding.run {
        profileImage.setOnSingleClickListener {
            pickImageFromGallery()
        }
        saveButton.setOnSingleClickListener {
            if (binding.firstNameEditText.text.toString()
                    .equals("") || binding.lastNameEditText.text.toString()
                    .equals("")
            ) {

            } else {
                val user_id = encryptedDataManager.getMYID()
                val updatedTodo = MyUsersModel(
                    user_id = encryptedDataManager.getMYID(),
                    first_name = binding.firstNameEditText.text.toString(),
                    last_name = binding.lastNameEditText.text.toString(),
                    email = binding.emailEditText.text.toString(),
                    password = encryptedDataManager.getMyPassword(),
                    imgurl = myImage.toString(),
                )
                profileViewModel.updateTodoByEmail(user_id, updatedTodo)


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
             binding.profileImage.loadImage(imageUri.toString(), requireActivity())
            hideLoadingDialog()

            getFileFromUri(requireActivity(), imageUri)?.let {
                viewModel.doCreateArticle(
                    "image", "Simple upload", "test",
                    it
                )
            }
        }
    }




    private fun observeArticle() {
        lifecycleScope.launch {
            viewModel.articleSharedFlow.collect {
                handleViewState(it)
            }
        }
    }

    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.viewState.collect { state ->
                when (state) {

                    is ProfileViewState.Loading ->  showLoadingDialog(R.string.loading)
                    is ProfileViewState.Initial -> {
                        hideLoadingDialog()                    }



                    is ProfileViewState.Success -> {
                        profileViewModel.getDetailsByEmail(encryptedDataManager.getMYID())
                        hideLoadingDialog()

                    }

                    is ProfileViewState.Error -> {
                        Toast.makeText(requireActivity(), state.message, Toast.LENGTH_LONG).show()
                        hideLoadingDialog()
                    }

                    is ProfileViewState.SuccessDelete -> {
                        Toast.makeText(requireActivity(), state.msg, Toast.LENGTH_LONG).show()
                        hideLoadingDialog()
                    }

                    is ProfileViewState.SuccessProfile -> {
                        binding.emailEditText.setText(state.todos.get(0).email)
                        binding.firstNameEditText.setText(state.todos.get(0).first_name)
                        binding.lastNameEditText.setText(state.todos.get(0).last_name)
                            binding.profileImage.loadImage(state.todos.get(0).imgurl, requireActivity())
                        myImage=state.todos.get(0).imgurl
                        hideLoadingDialog()
                    }
                    else ->Unit


                }
            }
        }
    }


    private fun handleViewState(viewState: CreateArticleViewState) {
        when (viewState) {
            is CreateArticleViewState.Loading ->  showLoadingDialog(R.string.loading)
            is CreateArticleViewState.Success -> {


                binding.profileImage.loadImage(viewState.myImageData.link, requireActivity())
                myImage = viewState.myImageData.link
                hideLoadingDialog()

            }

            is CreateArticleViewState.PopupError -> {
                hideLoadingDialog()
                Toast.makeText(requireActivity(), viewState.toString(), Toast.LENGTH_SHORT)
                    .show()
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            is CreateArticleViewState.InputError -> {
                // hideLoadingDialog()
                handleInputError(viewState.errorData ?: ErrorsData())
            }
        }
    }

    private fun handleInputError(errorsData: ErrorsData) {

        if (errorsData.image?.get(0)?.isNotEmpty() == true)
            Toast.makeText(requireActivity(), errorsData.image?.get(0), Toast.LENGTH_SHORT).show()
    }


    private fun showLoadingDialog(@StringRes strId: Int) {
        (requireActivity() as MainActivity ).showLoadingDialog(strId)
    }

    private fun hideLoadingDialog() {
        (requireActivity() as MainActivity ).hideLoadingDialog()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

    }



}