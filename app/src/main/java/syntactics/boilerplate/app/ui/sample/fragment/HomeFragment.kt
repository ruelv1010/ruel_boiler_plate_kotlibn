package syntactics.boilerplate.app.ui.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope


import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import syntactics.android.app.databinding.FragmentHomeBinding
import syntactics.boilerplate.app.security.AuthEncryptedDataManager
import syntactics.boilerplate.app.ui.sample.activity.MainActivity
import syntactics.boilerplate.app.ui.sample.viewmodel.LoginViewModel
import syntactics.boilerplate.app.ui.sample.viewmodel.LoginViewState
import syntactics.boilerplate.app.utils.setOnSingleClickListener
import syntactics.boilerplate.app.utils.showPopupError

@AndroidEntryPoint
class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private val activity by lazy { requireActivity() as MainActivity }
    private lateinit var encryptedDataManager: AuthEncryptedDataManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        observeUserInfo()
        encryptedDataManager = AuthEncryptedDataManager()


    }

    private fun observeUserInfo(){
        lifecycleScope.launch {
            viewModel.loginSharedFlow.collect{
                handleViewState(it)
            }
        }
    }

    private fun handleViewState(viewState: LoginViewState){
        when(viewState){
            is LoginViewState.Loading -> Unit
            is LoginViewState.SuccessGetUserInfo -> {
                Toast.makeText(requireActivity(), viewState.userLocalData.getFullName(), Toast.LENGTH_SHORT).show()
            }
            is LoginViewState.PopupError -> {
                showPopupError(requireActivity(), childFragmentManager, viewState.errorCode, viewState.message)
            }
            else -> Unit
        }
    }

    private fun setClickListeners() = binding.run {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}