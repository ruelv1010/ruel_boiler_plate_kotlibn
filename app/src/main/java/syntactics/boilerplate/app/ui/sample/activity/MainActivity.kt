package syntactics.boilerplate.app.ui.sample.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView


import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import syntactics.android.app.R
import syntactics.android.app.databinding.ActivityMainBinding
import syntactics.boilerplate.app.utils.dialog.CommonDialog
import syntactics.boilerplate.app.utils.setOnSingleClickListener

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var loadingDialog: CommonDialog? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController
    private var isActive: String? = null
    var currentTutorialStep = 0 // To track which step the tutorial is on
    var currentTapTargetView: TapTargetView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupNavigationComponent()
        setClickListener()
        executeTutorials()

    }

    fun setupNavigationComponent() {
        binding.run {
            navView = tabBottomNavigationView
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.tabNavHostFragment) as NavHostFragment
            navController = navHostFragment.navController
            navView.setupWithNavController(navController)
        }
    }


    fun executeTutorials() {
        CoroutineScope(Dispatchers.Main).launch {
            tutorialHome()
        }
    }

    fun tutorialHome() {
        highlightMenuItem(
            navView,
            R.id.navigation_home,
            "Explore Home",
            "Tap here to navigate to the Home section.",
            R.id.navigation_create
        )
    }

    fun tutorialCreate() {
        highlightMenuItem(
            navView,
            R.id.navigation_create,
            "Explore Create",
            "Tap here to navigate to the Create Todo.",
            R.id.navigation_article
        )
    }

    fun tutorialList() {
        highlightMenuItem(
            navView,
            R.id.navigation_article,
            "Explore List of Todo",
            "Tap here to navigate to your to-do list.",
            R.id.navigation_profile
        )
    }

    fun tutorialProfile() {
        highlightMenuItem(
            navView,
            R.id.navigation_profile,
            "Explore Profile",
            "Tap here to navigate to your Profile.",
            -2
        )
    }

    fun highlightMenuItem(
        bottomNavigationView: BottomNavigationView,
        menuItemId: Int,
        title: String,
        description: String,
        nextMenuItemId: Int
    ) {
        val menuItemView = bottomNavigationView.findViewById<View>(menuItemId)
        menuItemView?.let {
            val tapTarget = TapTarget.forView(
                it,
                title,
                description
            ).apply {
                outerCircleColor(R.color.teal_200)
                targetCircleColor(android.R.color.white)
                titleTextSize(20)
                descriptionTextSize(14)
                cancelable(false)
            }


            currentTapTargetView = TapTargetView.showFor(
                this,
                tapTarget,
                object : TapTargetView.Listener() {
                    override fun onTargetClick(view: TapTargetView?) {
                        super.onTargetClick(view)

                        hideTutorial()


                        if (nextMenuItemId != -2) {

                            when (nextMenuItemId) {
                                R.id.navigation_create -> tutorialCreate()
                                R.id.navigation_article -> tutorialList()
                                R.id.navigation_profile -> tutorialProfile()
                            }
                        }
                    }

                    override fun onTargetCancel(view: TapTargetView?) {
                        super.onTargetCancel(view)

                    }
                }
            )
        }
    }

    fun hideTutorial() {
        currentTapTargetView?.dismiss(false)
        currentTapTargetView = null
    }

    private fun setClickListener() = binding.run {
        logoutImageView.setOnSingleClickListener {
            openLogoutConfirmation()
        }
    }

    private fun openLogoutConfirmation() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.logout_title_lbl))
        builder.setMessage(getString(R.string.logout_desc_lbl))
        builder.setPositiveButton(getString(R.string.logout_btn)) { _, _ ->
            val intent = SplashScreenActivity.getIntent(this)
            startActivity(intent)
            finishAffinity()
        }
        builder.setNegativeButton(getString(R.string.logout_cancel_btn), null)
        builder.show()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }


    fun showLoadingDialog(@StringRes strId: Int) {
        if (loadingDialog == null) {
            loadingDialog = CommonDialog.getLoadingDialogInstance(
                message = getString(strId)
            )
        }
        loadingDialog?.show(supportFragmentManager)
    }

    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}