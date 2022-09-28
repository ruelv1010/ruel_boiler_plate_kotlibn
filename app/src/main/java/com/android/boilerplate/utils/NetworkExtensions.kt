package com.android.boilerplate.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun File.asNetWorkRequestBody(mimeType: String): RequestBody {
    return this.asRequestBody(mimeType.toMediaTypeOrNull())
}

fun String?.createPartFromString(): RequestBody {
    return this.orEmpty().toRequestBody("text/plain".toMediaTypeOrNull())
}