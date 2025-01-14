package syntactics.boilerplate.app.ui.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope


import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import syntactics.android.app.R
import syntactics.android.app.databinding.FragmentHomeBinding
import syntactics.boilerplate.app.data.model.MyUsersModel
import syntactics.boilerplate.app.security.AuthEncryptedDataManager
import syntactics.boilerplate.app.ui.article.viewmodel.ProfileViewModel
import syntactics.boilerplate.app.ui.article.viewmodel.ProfileViewState
import syntactics.boilerplate.app.ui.sample.activity.MainActivity
import syntactics.boilerplate.app.ui.sample.viewmodel.LoginViewModel
import syntactics.boilerplate.app.ui.sample.viewmodel.LoginViewState
import syntactics.boilerplate.app.utils.dialog.CommonDialog
import syntactics.boilerplate.app.utils.loadImage
import syntactics.boilerplate.app.utils.setOnSingleClickListener
import syntactics.boilerplate.app.utils.showPopupError

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private val activity by lazy { requireActivity() as MainActivity }
    private lateinit var encryptedDataManager: AuthEncryptedDataManager
    private val profileViewModel: ProfileViewModel by viewModels()
    private var loadingDialog: CommonDialog? = null
    private var firstName: String?=null
    private var lastName: String?=null
    private var myEmail: String?=null
    private var myImg: String?=null
    private var isNEw: String?=null

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
        observeViewModel()
        encryptedDataManager = AuthEncryptedDataManager()

    }

    private fun showYesNoDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Tutorial")
        builder.setMessage("Need assist?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            activity.executeTutorials()
            encryptedDataManager.setIsNew("true")
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            encryptedDataManager.setIsNew("false")
            val updatedTodo = MyUsersModel(
                user_id = encryptedDataManager.getMYID(),
                first_name = encryptedDataManager.getFirstName(),
                last_name = encryptedDataManager.getLastName(),
                email = encryptedDataManager.getMyEmail(),
                password = encryptedDataManager.getMyPassword(),
                imgurl = encryptedDataManager.getIMG(),
                isfirst = "false"
            )
            profileViewModel.updateTodoByEmail(
                encryptedDataManager.getMYID(),
                updatedTodo
            )
            dialog.dismiss()
        }
        builder.create().show()
    }


    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.viewState.collect { state ->
                when (state) {

                    is ProfileViewState.Loading -> showLoadingDialog(R.string.loading)
                    is ProfileViewState.Initial -> {
                        hideLoadingDialog()
                    }

                    is ProfileViewState.Success -> {
                        encryptedDataManager.setIsNew("false")
                        hideLoadingDialog()

                    }

                    is ProfileViewState.Error -> {
                        Toast.makeText(requireActivity(), state.message, Toast.LENGTH_LONG).show()
                        hideLoadingDialog()
                    }


                    is ProfileViewState.SuccessProfile -> {
                        if (state.todos.isNotEmpty() && state.todos[0].isfirst == "true") {
                            if (encryptedDataManager.getIsNew() != "true") { // Prevent duplicate dialogs
                                showYesNoDialog()
                            }
                        } else {
                            encryptedDataManager.setFirtName(state.todos[0].first_name.toString())
                            encryptedDataManager.setLastName(state.todos[0].last_name.toString())
                            myImg = state.todos[0].imgurl
                        }
                        hideLoadingDialog()
                    }

                    else -> Unit


                }
            }
        }
    }

    private fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
            loadingDialog?.show(childFragmentManager)
        }
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }


    override fun onResume() {
        super.onResume()
        profileViewModel.getDetailsByEmail(encryptedDataManager.getMYID())
    }

    private fun observeUserInfo() {
        lifecycleScope.launch {
            viewModel.loginSharedFlow.collect {
                handleViewState(it)
            }
        }
    }

    private fun handleViewState(viewState: LoginViewState) {
        when (viewState) {
            is LoginViewState.Loading -> Unit
            is LoginViewState.SuccessGetUserInfo -> {
                Toast.makeText(
                    requireActivity(),
                    viewState.userLocalData.getFullName(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            is LoginViewState.PopupError -> {
                showPopupError(
                    requireActivity(),
                    childFragmentManager,
                    viewState.errorCode,
                    viewState.message
                )
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