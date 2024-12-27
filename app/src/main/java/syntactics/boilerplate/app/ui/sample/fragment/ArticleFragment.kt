package syntactics.boilerplate.app.ui.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import syntactics.android.app.R
import syntactics.android.app.databinding.FragmentArticleBinding
import syntactics.boilerplate.app.data.model.TodoModel
import syntactics.boilerplate.app.data.repositories.article.response.ArticleData
import syntactics.boilerplate.app.security.AuthEncryptedDataManager
import syntactics.boilerplate.app.ui.article.activity.CreateArticleActivity
import syntactics.boilerplate.app.ui.article.dialog.UpdateDialog
import syntactics.boilerplate.app.ui.article.viewmodel.ArticleListViewModel
import syntactics.boilerplate.app.ui.article.viewmodel.ArticleListViewState
import syntactics.boilerplate.app.ui.article.viewmodel.TodoViewModel
import syntactics.boilerplate.app.ui.article.viewmodel.TodoViewState
import syntactics.boilerplate.app.ui.sample.activity.MainActivity
import syntactics.boilerplate.app.ui.sample.adapter.ArticleAdapter
import syntactics.boilerplate.app.utils.EndlessRecyclerScrollListener
import syntactics.boilerplate.app.utils.FirebaseHelper
import syntactics.boilerplate.app.utils.dialog.CommonDialog
import syntactics.boilerplate.app.utils.setOnSingleClickListener
import syntactics.boilerplate.app.utils.showPopupError

@AndroidEntryPoint
class ArticleFragment : Fragment(), ArticleAdapter.ArticleCallback,
    SwipeRefreshLayout.OnRefreshListener {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var articleAdapter: ArticleAdapter? = null
    private val viewModel: ArticleListViewModel by viewModels()
    private val todoViewModel: TodoViewModel by viewModels()
    private var scrollListener: EndlessRecyclerScrollListener? = null
    private var loadingDialog: CommonDialog? = null
    private val activity by lazy { requireActivity() as MainActivity }
    private lateinit var encryptedDataManager: AuthEncryptedDataManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentArticleBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArticleList()
        setupClickListeners()
        observeViewModel()
        encryptedDataManager = AuthEncryptedDataManager()
    }

    private fun setupArticleList() = binding.run {
        binding.swipeRefreshLayout.setOnRefreshListener(this@ArticleFragment)
        articleAdapter = ArticleAdapter(requireActivity(), this@ArticleFragment)
        linearLayoutManager = LinearLayoutManager(context)
        articleRecyclerView.layoutManager = linearLayoutManager
        articleRecyclerView.adapter = articleAdapter

    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }


    private fun observeViewModel() {
        // Collect ViewState
        viewLifecycleOwner.lifecycleScope.launch {
            todoViewModel.viewState.collect { state ->
                when (state) {
                    is TodoViewState.Initial -> {
                    }

                    is TodoViewState.Loading -> showLoadingDialog(R.string.loading)

                    is TodoViewState.Success -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        hideLoadingDialog()
                        articleAdapter?.clear()
                        articleAdapter?.appendData(state.todos)
                    }

                    is TodoViewState.Error -> {
                        Toast.makeText(requireActivity(), state.message, Toast.LENGTH_LONG).show()

                    }

                    is TodoViewState.SuccessDelete -> {
                        Toast.makeText(requireActivity(), state.msg, Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
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


    private fun setupClickListeners() = binding.run {
        createArticleButton.setOnSingleClickListener {


        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: TodoModel) {

        val updateDialog =
            UpdateDialog.newInstance(object : UpdateDialog.ProfileSaveDialogCallBack {
                override fun onMyAccountClicked(dialog: UpdateDialog) {
                todoViewModel.deleteTodo(data.id.toString())
                }

                override fun onSuccess(dialog: UpdateDialog, title: String, desc: String) {
                    todoViewModel.updateTodo(
                        todo = TodoModel(
                            id = data.id,
                            title = title,
                            description = desc
                        )
                    )
                }

            }, title =data.title.toString(), desc =data.description.toString())

        updateDialog.show(childFragmentManager, UpdateDialog.TAG)
    }

    override fun onRefresh() {
        todoViewModel.fetchTodos(encryptedDataManager.getMYID())

    }
}