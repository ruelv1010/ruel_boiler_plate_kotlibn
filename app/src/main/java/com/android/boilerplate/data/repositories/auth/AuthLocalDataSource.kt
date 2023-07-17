package com.android.boilerplate.data.repositories.auth

import com.android.boilerplate.data.local.BoilerPlateDatabase
import com.android.boilerplate.data.local.UserDao
import com.android.boilerplate.data.local.UserLocalData
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val userDao: UserDao
){
    suspend fun login(user : UserLocalData) = userDao.login(user)

    suspend fun updateToken(userId: Int,token: String) = userDao.updateToken(userId, token)
    suspend fun logout(accessToken: String) = userDao.logout(accessToken)
    suspend fun getUserInfo(access_token : String) = userDao.getUserInfo(access_token)
}