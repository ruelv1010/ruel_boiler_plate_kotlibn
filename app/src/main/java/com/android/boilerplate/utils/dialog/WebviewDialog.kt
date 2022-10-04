package com.android.boilerplate.utils.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.android.boilerplate.R
import com.android.boilerplate.databinding.DialogWebviewBinding
import com.android.boilerplate.utils.ObservableWebView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class WebviewDialog private constructor() : BottomSheetDialogFragment() {

    private var viewBinding: DialogWebviewBinding? = null
    private val url by lazy { arguments?.getString(EXTRA_URL).orEmpty() }
    private var currentWebScrollY = 0

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val height = (resources.displayMetrics.heightPixels * .95).toInt()
        bottomSheet?.layoutParams?.height = height
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_webview,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding = DialogWebviewBinding.bind(view)

        initWebView()
        setWebClient()
        loadUrl(url)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            val height = (resources.displayMetrics.heightPixels * .95).toInt()
            behavior.peekHeight = height
            behavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING && currentWebScrollY > 0) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                    } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss()
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        viewBinding?.apply {

            webView.settings.javaScriptEnabled = true
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            webView.settings.domStorageEnabled = true
            webView.webViewClient = object : WebViewClient() {
                @SuppressLint("WebViewClientOnReceivedSslError")
                override
                fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    handleOnReceiveErrorSsl(handler)
                }
            }
            webView.onScrollChangedCallback = object : ObservableWebView.OnScrollChangedCallback {
                override fun onScroll(
                    currentHorizontalScroll: Int,
                    currentVerticalScroll: Int,
                    oldHorizontalScroll: Int,
                    oldcurrentVerticalScroll: Int
                ) {
                    currentWebScrollY = currentVerticalScroll
                }

            }
        }
    }

    private fun setWebClient() {
        viewBinding?.apply {

            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progressBar.isVisible = newProgress < 100
                    webView.isInvisible = newProgress < 100
                    progressBar.progress = newProgress
                    super.onProgressChanged(view, newProgress)
                }
            }

            dialog?.setOnCancelListener { dialog ->
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    dialog.dismiss()
                }
            }

            dialog?.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        dialog?.dismiss()
                    }
                }
                true
            }
        }
    }

    private fun loadUrl(pageUrl: String) {
        viewBinding?.webView?.loadUrl(pageUrl.toSecuredUri())
    }

    private fun handleOnReceiveErrorSsl(handler: SslErrorHandler?) {

    }

    private fun String.toSecuredUri(): String {
        return Uri.parse(this).buildUpon().scheme("https").toString()

    }

    companion object {
        private val TAG = WebviewDialog::class.java.simpleName
        private const val EXTRA_URL = "EXTRA_URL"

        fun openDialog(
            fragmentManager: FragmentManager,
            url: String
        ) {
            WebviewDialog().apply {
                arguments = bundleOf(
                    EXTRA_URL to url
                )
            }
                .show(fragmentManager, TAG)
        }
    }
}