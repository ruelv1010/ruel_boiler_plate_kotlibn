package syntactics.boilerplate.app.data.repositories.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import syntactics.boilerplate.app.data.repositories.auth.request.LoginRequest
import syntactics.boilerplate.app.data.repositories.auth.response.LoginResponse

interface AuthService {
    @POST("api/auth/login.json")
    suspend fun doLogin(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/refresh-token.json")
    suspend fun doRefreshToken(): Response<LoginResponse>
}