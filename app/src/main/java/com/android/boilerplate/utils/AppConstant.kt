package com.android.boilerplate.utils

object AppConstant {
    val TOKEN_NOT_PROVIDED = "TOKEN_NOT_PROVIDED"
    val TOKEN_EXPIRED = "TOKEN_EXPIRED"
    val TOKEN_INVALID = "TOKEN_INVALID"
    val INVALID_ID_AUTH_USER = "INVALID_ID_AUTH_USER"
    val INVALID_TOKEN = "INVALID_TOKEN"
    val ACCOUNT_LOGOUT = "ACCOUNT_LOGOUT"
    val EXPIRED_TOKEN = "EXPIRED_TOKEN"

    fun isSessionStatusCode(code: String): Boolean{
        return code == TOKEN_NOT_PROVIDED ||
                code == TOKEN_EXPIRED ||
                code == TOKEN_INVALID ||
                code == INVALID_ID_AUTH_USER ||
                code == INVALID_TOKEN ||
                code == ACCOUNT_LOGOUT ||
                code == EXPIRED_TOKEN
    }
}