package syntactics.boilerplate.app.ui.sample.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.app.R
import com.android.app.data.model.ErrorsData
import com.android.app.databinding.ActivityLoginBinding
import com.android.app.ui.sample.viewmodel.LoginViewModel
import com.android.app.ui.sample.viewmodel.LoginViewState
import syntactics.boilerplate.app.utils.dialog.CommonDialog
import com.android.app.utils.setOnSingleClickListener
import com.android.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var loadingDialog: syntactics.boilerplate.app.utils.dialog.CommonDialog? = null
    private val viewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupClickListener()
        observeLogin()
    }

    private fun setupClickListener() = binding.run{
        loginButton.setOnSingleClickListener {
            viewModel.doLoginAccount(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }
    }

    private fun observeLogin(){
        lifecycleScope.launch {
            viewModel.loginSharedFlow.collect{
                handleViewState(it)
            }
        }
    }

    private fun handleViewState(viewState: LoginViewState){
        when(viewState){
            is LoginViewState.Loading -> showLoadingDialog(R.string.login_loading)
            is LoginViewState.Success -> {
                hideLoadingDialog()
                val intent = MainActivity.getIntent(this@LoginActivity)
                startActivity(intent)
                Toast.makeText(this, viewState.message, Toast.LENGTH_SHORT).show()
            }
            is LoginViewState.PopupError -> {
                hideLoadingDialog()
                showPopupError(this@LoginActivity, supportFragmentManager, viewState.errorCode, viewState.message)
            }
            is LoginViewState.InputError -> {
                hideLoadingDialog()
                handleInputError(viewState.errorData?: ErrorsData())
            }
            else -> Unit
        }
    }

    private fun handleInputError(errorsData: ErrorsData){
        if (errorsData.email?.get(0)?.isNotEmpty() == true) binding.emailEditText.error = errorsData.email?.get(0)
        if (errorsData.password?.get(0)?.isNotEmpty() == true) binding.passwordEditText.error = errorsData.password?.get(0)
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
        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}