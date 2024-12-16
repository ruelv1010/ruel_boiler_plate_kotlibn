package syntactics.boilerplate.app.ui.article.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.android.app.R
import com.android.app.data.model.ErrorsData
import com.android.app.databinding.ActivityCreateArticleBinding
import com.android.app.ui.article.viewmodel.CreateArticleViewModel
import com.android.app.ui.article.viewmodel.CreateArticleViewState
import syntactics.boilerplate.app.utils.dialog.CommonDialog
import com.android.app.utils.setOnSingleClickListener
import com.android.app.utils.showPopupError
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.launch


@AndroidEntryPoint
class CreateArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateArticleBinding
    private val viewModel: CreateArticleViewModel by viewModels()
    private var loadingDialog: syntactics.boilerplate.app.utils.dialog.CommonDialog? = null

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
            checkPermission()
        }
        createArticleButton.setOnSingleClickListener {
            viewModel.doCreateArticle(
                titleEditText.text.toString(),
                descEditText.text.toString(),
                viewModel.imageFile
            )
        }
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


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_WRITE_EXTERNAL
            )
        } else {
            openImagePickerDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PERMISSION_WRITE_EXTERNAL == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePickerDialog()
            } else {
                Toast.makeText(this,  "Permission Granted", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openImagePickerDialog() {
//        TedBottomPicker.with(this as FragmentActivity)
//            .setTitle("Upload Image")
//            .showCameraTile(true)
//            .showGalleryTile(true)
//            .show {
//                viewModel.imageFile = it.toFile()
//                Glide.with(this)
//                    .load(it)
//                    .placeholder(R.color.color_primary)
//                    .error(R.color.color_primary)
//                    .into(binding.articleImageView)
//            }
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
            return Intent(context, CreateArticleActivity::class.java)
        }
        private const val PERMISSION_WRITE_EXTERNAL = 101
    }
}