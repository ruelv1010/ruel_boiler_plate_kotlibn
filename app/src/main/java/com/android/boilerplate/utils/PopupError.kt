package com.android.boilerplate.utils

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.android.boilerplate.R
import com.android.boilerplate.utils.dialog.CommonsErrorDialog

fun showPopupError(
    context: Context,
    fragmentManager: FragmentManager,
    errorCode: PopupErrorState,
    errorMessage: String
) {
    val message = when (errorCode) {
        PopupErrorState.NetworkError -> context.getString(R.string.common_network_msg)
        PopupErrorState.HttpError -> errorMessage
        else -> context.getString(R.string.common_something_went_wrong_msg)
    }

    CommonsErrorDialog.openDialog(
        fragmentManager,
        message = message
    )
}