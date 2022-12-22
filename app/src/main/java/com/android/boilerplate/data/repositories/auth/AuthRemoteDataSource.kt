package com.android.boilerplate.data.repositories.auth

import com.android.boilerplate.data.repositories.auth.request.LoginRequest
import com.android.boilerplate.data.repositories.auth.response.LoginResponse
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(private val authService: AuthService)  {

    suspend fun doLogin(email: String, password: String): LoginResponse{
        val request = LoginRequest(email, password)
        val response = authService.doLogin(request)

        //Check if response code is 200 else it will throw HttpException
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        //Will automatically throw a NullPointerException when response.body() is Null

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

}