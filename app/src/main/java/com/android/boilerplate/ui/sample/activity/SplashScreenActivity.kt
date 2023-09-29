package com.android.boilerplate.ui.sample.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.android.boilerplate.databinding.ActivitySplashscreenBinding
import com.android.boilerplate.ui.sample.viewmodel.SplashViewModel
import com.android.boilerplate.ui.sample.viewmodel.SplashViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashscreenBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        observeRefreshToken()
        viewModel.doRefreshToken()
    }

    private fun observeRefreshToken() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.splashStateFlow.collect { viewState ->
                    handleViewState(viewState)
                }
            }
        }
    }

    private fun handleViewState(viewState: SplashViewState) {
        when (viewState) {
            is SplashViewState.Loading -> Unit
            is SplashViewState.SuccessRefreshToken -> {
                if (viewState.status){
                        val intent = MainActivity.getIntent(this)
                        startActivity(intent)
                        this.finishAffinity()
                }else{
                    val intent = LoginActivity.getIntent(this)
                    startActivity(intent)
                    this.finish()
                }
            }
            is SplashViewState.PopupError -> {
                val intent = LoginActivity.getIntent(this)
                startActivity(intent)
                this.finish()
            }
            is SplashViewState.Idle -> Unit
        }
    }

    companion object {
        private const val DELAY = 3000L
        fun getIntent(context: Context): Intent {
            return Intent(context, SplashScreenActivity::class.java)
        }
    }
}