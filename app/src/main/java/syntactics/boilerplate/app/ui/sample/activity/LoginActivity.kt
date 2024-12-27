package syntactics.boilerplate.app.ui.sample.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import syntactics.android.app.R
import syntactics.android.app.databinding.ActivityLoginBinding
import syntactics.boilerplate.app.data.model.ErrorsData
import syntactics.boilerplate.app.security.AuthEncryptedDataManager
import syntactics.boilerplate.app.ui.sample.viewmodel.LoginViewModel
import syntactics.boilerplate.app.ui.sample.viewmodel.LoginViewState
import syntactics.boilerplate.app.utils.setOnSingleClickListener
import syntactics.boilerplate.app.utils.showPopupError

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var encryptedDataManager: AuthEncryptedDataManager
    private lateinit var binding: ActivityLoginBinding
    private var loadingDialog: syntactics.boilerplate.app.utils.dialog.CommonDialog? = null
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupClickListener()
        observeLogin()
        auth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this);
        encryptedDataManager = AuthEncryptedDataManager()
        setupTutorialSignup()
    }

    private fun setupFirstTutorialLogin() = binding.run {
        TapTargetView.showFor(
            this@LoginActivity,  // The current activity
            TapTarget.forView(
                loginButton,
                "This is login button",
                "Click here to perform an action to login"
            )
                .outerCircleColor(R.color.purple_500)
                .outerCircleAlpha(0.96f)
                .targetCircleColor(R.color.white)
                .titleTextSize(20)
                .titleTextColor(R.color.white)
                .descriptionTextSize(16)
                .descriptionTextColor(R.color.white)
                .textColor(R.color.white)
                .dimColor(R.color.black)
                .drawShadow(true)
                .cancelable(true)
                .tintTarget(true)
                .transparentTarget(true)
                .targetRadius(35),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)

                }

                override fun onTargetCancel(view: TapTargetView) {
                    super.onTargetCancel(view)
                }
            }
        )

    }

    private fun setupTutorialSignup() = binding.run {
        TapTargetView.showFor(
            this@LoginActivity,  // The current activity
            TapTarget.forView(
                signUpTextView,
                "This is Register button",
                "If you don't have account yet click here"
            )
                .outerCircleColor(R.color.purple_500)
                .outerCircleAlpha(0.96f)
                .targetCircleColor(R.color.white)
                .titleTextSize(20)
                .titleTextColor(R.color.white)
                .descriptionTextSize(16)
                .descriptionTextColor(R.color.white)
                .textColor(R.color.white)
                .dimColor(R.color.black)
                .drawShadow(true)
                .cancelable(true)
                .tintTarget(true)
                .transparentTarget(true)
                .targetRadius(35),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    setupFirstTutorialLogin()

                }

                override fun onTargetCancel(view: TapTargetView) {
                    super.onTargetCancel(view)
                    setupFirstTutorialLogin()
                }
            }
        )

    }

    private fun setupClickListener() = binding.run {
        loginButton.setOnSingleClickListener {
            if (passwordEditText.text.toString() == "" || emailEditText.text.toString() == "")
            {
                Toast.makeText(this@LoginActivity, "Please complete all fields", Toast.LENGTH_SHORT).show()
            }else {
                signIn(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }

        }
        signUpTextView.setOnSingleClickListener {
            val intent = RegisterActivity.getIntent(this@LoginActivity)
            startActivity(intent)
        }
    }


    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    val user = auth.currentUser
                    encryptedDataManager.setID(auth.uid.toString())
                    Toast.makeText(this, "Welcome back: ${user?.email}", Toast.LENGTH_SHORT).show()
                    val intent = MainActivity.getIntent(this@LoginActivity)
                    startActivity(intent)
                } else {
                    // Sign-in failed
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
                val intent = MainActivity.getIntent(this@LoginActivity)
                startActivity(intent)
                Toast.makeText(this, viewState.message, Toast.LENGTH_SHORT).show()
            }

            is LoginViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(
                    this@LoginActivity,
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
            return Intent(context, LoginActivity::class.java)
        }
    }
}