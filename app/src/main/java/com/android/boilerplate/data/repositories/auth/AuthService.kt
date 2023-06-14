package com.android.boilerplate.data.repositories.auth

import com.android.boilerplate.data.repositories.auth.request.LoginRequest
import com.android.boilerplate.data.repositories.auth.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/auth/login.json")
    suspend fun doLogin(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/refresh-token.json")
    suspend fun doRefreshToken(): Response<LoginResponse>
}