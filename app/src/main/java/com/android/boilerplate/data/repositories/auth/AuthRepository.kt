package com.android.boilerplate.data.repositories.auth

import com.android.boilerplate.data.repositories.auth.request.LoginRequest
import com.android.boilerplate.data.repositories.auth.response.LoginResponse
import com.android.boilerplate.data.repositories.auth.response.UserData
import com.android.boilerplate.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val encryptedDataManager: AuthEncryptedDataManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun doLogin(email: String, password: String): Flow<LoginResponse> {
        return flow {
            val response = authRemoteDataSource.doLogin(email, password)
            val userInfo = response.data?: UserData()
            val token = response.token.orEmpty()
            encryptedDataManager.setAccessToken(token)
            encryptedDataManager.setUserBasicInfo(userInfo)
            emit(response)
        }.flowOn(ioDispatcher)
    }

}