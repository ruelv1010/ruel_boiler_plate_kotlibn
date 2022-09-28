package com.android.boilerplate.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.io.InputStream

fun getBitmapFromAsset(context: Context, imageName: String): Bitmap? {
    val assetManager = context.assets

    val istr: InputStream
    var bitmap: Bitmap? = null
    try {
        istr = assetManager.open(imageName)
        bitmap = BitmapFactory.decodeStream(istr)
    } catch (e: IOException) {
        // handle exception
    }

    return bitmap
}