package syntactics.boilerplate.app.ui.sample.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import syntactics.android.app.R
import syntactics.android.app.databinding.ActivityRegisterBinding
import syntactics.boilerplate.app.data.model.ErrorsData
import syntactics.boilerplate.app.data.model.MyUsersModel
import syntactics.boilerplate.app.data.model.TodoModel
import syntactics.boilerplate.app.ui.sample.viewmodel.LoginViewModel
import syntactics.boilerplate.app.ui.sample.viewmodel.LoginViewState
import syntactics.boilerplate.app.utils.FirebaseHelper
import syntactics.boilerplate.app.utils.FirebaseUsersHelper
import syntactics.boilerplate.app.utils.dialog.CommonDialog
import syntactics.boilerplate.app.utils.setOnSingleClickListener
import syntactics.boilerplate.app.utils.showPopupError

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var loadingDialog: CommonDialog? = null
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private val firebaseHelper = FirebaseUsersHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupClickListener()
        observeLogin()
        auth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this);

    }

    private fun setupClickListener() = binding.run {
        loginButton.setOnSingleClickListener {
            if (passwordEditText.text.toString() == "" ){
                passwordEditText.error="Password cannot be empty"
            }
            if (passwordConfirmEditText.text.toString() == "" ){
                passwordConfirmEditText.error="Confirm Password cannot be empty"
            }
            if (emailEditText.text.toString() == "" ){
                emailEditText.error="Email cannot be empty"
            }
            if (firstNameEditText.text.toString() == "" ){
                firstNameEditText.error="First Name cannot be empty"
            }
            if (lastNameEditText.text.toString() == "" ){
                lastNameEditText.error="Last Name cannot be empty"
            }



            else if (passwordEditText.text.toString() == passwordConfirmEditText.text.toString()&&
                passwordEditText.text.toString() != ""&&
                passwordConfirmEditText.text.toString() != ""&&
                emailEditText.text.toString() != ""&&
                firstNameEditText.text.toString() != ""&&
                lastNameEditText.text.toString() != "") {


                signUp(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            } else {
                Toast.makeText(this@RegisterActivity, "Password not match", Toast.LENGTH_SHORT)
                    .show()
            }

        }




        signUpTextView.setOnSingleClickListener {
            val intent = LoginActivity.getIntent(this@RegisterActivity)
            startActivity(intent)
        }
    }


    private fun saveUsersFirebase(myUsersModel: MyUsersModel) {
        firebaseHelper.addTodoItem(myUsersModel, {
            val intent = LoginActivity.getIntent(this@RegisterActivity)
            startActivity(intent)
        }, { error ->

        })
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val myUsersModel = MyUsersModel(
                        email = user?.email,
                        first_name = binding.firstNameEditText.text.toString(),
                        imgurl = user?.email,
                        last_name = binding.lastNameEditText.text.toString(),
                        password = binding.passwordEditText.text.toString(),
                        user_id = user?.uid,
                        isfirst = "true"
                    )
                    saveUsersFirebase(myUsersModel)
                    Toast.makeText(this, "Sign-up successful: ${user?.email}", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // Sign-up failed
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun observeLogin() {
        lifecycleScope.launch {
            viewModel.loginSharedFlow.collect {
                handleViewState(it)
            }
        }
    }

    private fun handleViewState(viewState: LoginViewState) {
        when (viewState) {
            is LoginViewState.Loading -> showLoadingDialog(R.string.login_loading)
            is LoginViewState.Success -> {
                hideLoadingDialog()
                val intent = MainActivity.getIntent(this@RegisterActivity)
                startActivity(intent)
                Toast.makeText(this, viewState.message, Toast.LENGTH_SHORT).show()
            }

            is LoginViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    this@RegisterActivity,
                    supportFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
            }

            is LoginViewState.InputError -> {
                hideLoadingDialog()
                handleInputError(viewState.errorData ?: ErrorsData())
            }

            else -> Unit
        }
    }

    private fun handleInputError(errorsData: ErrorsData) {
        if (errorsData.email?.get(0)?.isNotEmpty() == true) binding.emailEditText.error =
            errorsData.email?.get(0)
        if (errorsData.password?.get(0)?.isNotEmpty() == true) binding.passwordEditText.error =
            errorsData.password?.get(0)
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog =
                syntactics.boilerplate.app.utils.dialog.CommonDialog.getLoadingDialogInstance(
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
        fun getIntent(context: Context): Intent {
            return Intent(context, RegisterActivity::class.java)
        }
    }
}