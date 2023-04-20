package com.android.boilerplate.ui.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.boilerplate.R
import com.android.boilerplate.data.model.ErrorsData
import com.android.boilerplate.data.repositories.auth.request.LoginRequest
import com.android.boilerplate.data.repositories.auth.response.LoginResponse
import com.android.boilerplate.databinding.FragmentHomeBinding
import com.android.boilerplate.ui.sample.activity.MainActivity
import com.android.boilerplate.ui.sample.viewmodel.LoginViewModel
import com.android.boilerplate.ui.sample.viewmodel.LoginViewState
import com.android.boilerplate.utils.dialog.WebviewDialog
import com.android.boilerplate.utils.setOnSingleClickListener
import com.android.boilerplate.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment: Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

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
        viewModel.getUserInfo()
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
        loadWebViewDialogButton.setOnSingleClickListener {
            openWebViewDialog()
        }
    }

    private fun openWebViewDialog(){
        WebviewDialog.openDialog(
            childFragmentManager,
            "https://www.pmti.biz/"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}