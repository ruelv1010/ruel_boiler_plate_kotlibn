package com.android.boilerplate.utils

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.room.Room
import com.android.boilerplate.R
import com.android.boilerplate.base.BaseApplication
import com.android.boilerplate.data.local.BoilerPlateDatabase
import com.android.boilerplate.data.local.UserDao
import com.android.boilerplate.data.local.UserLocalData
import com.android.boilerplate.security.AuthEncryptedDataManager
import com.android.boilerplate.ui.sample.activity.LoginActivity
import com.android.boilerplate.ui.sample.activity.SplashScreenActivity
import com.android.boilerplate.utils.dialog.CommonsErrorDialog

fun showPopupError(
    context: Context,
    fragmentManager: FragmentManager,
    errorCode: PopupErrorState,
    errorMessage: String
) {
    val message = when (errorCode) {
        PopupErrorState.NetworkError -> context.getString(R.string.common_network_msg)
        PopupErrorState.HttpError,
        PopupErrorState.SessionError -> errorMessage
        else -> context.getString(R.string.common_something_went_wrong_msg)
    }

    CommonsErrorDialog.openDialog(
        fragmentManager,
        message = message
    ){
        //return to splash screen an delete all user data
        if (errorCode == PopupErrorState.SessionError){
            val intent = LoginActivity.getIntent(context)
            context.startActivity(intent)
            AuthEncryptedDataManager().clearUserInfo()
            //include this if app has local database
        }
    }
}