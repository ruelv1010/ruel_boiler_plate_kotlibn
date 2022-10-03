package com.android.boilerplate.ui.sample.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.android.boilerplate.databinding.ActivitySplashscreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        Handler().postDelayed({
            val intent = LoginActivity.getIntent(this)
            startActivity(intent)
            finish()
        }, DELAY)

    }


    companion object {
        private const val DELAY = 3000L
        fun getIntent(context: Context): Intent {
            return Intent(context, SplashScreenActivity::class.java)
        }
    }
}