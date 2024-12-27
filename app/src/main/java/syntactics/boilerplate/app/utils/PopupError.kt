package syntactics.boilerplate.app.utils

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentManager
import syntactics.android.app.R

import syntactics.boilerplate.app.security.AuthEncryptedDataManager
import syntactics.boilerplate.app.ui.sample.activity.LoginActivity
import syntactics.boilerplate.app.utils.dialog.CommonsErrorDialog

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
            (context as Activity).finishAffinity()
            AuthEncryptedDataManager().clearUserInfo()
            //include this if app has local database
        }
    }
}