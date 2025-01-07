package syntactics.boilerplate.app.data.repositories.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import syntactics.boilerplate.app.data.repositories.auth.request.LoginRequest
import syntactics.boilerplate.app.data.repositories.auth.response.LoginResponse
import java.net.HttpURLConnection
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(private val authService: AuthService)  {

    suspend fun doLogin(email: String, password: String): LoginResponse {
        val request = LoginRequest(email, password)
        val response = authService.doLogin(request)

        //Check if response code is 200 else it will throw HttpException
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        //Will automatically throw a NullPointerException when response.body() is Null

        return response.body() ?: throw NullPointerException("Response data is empty")
    }

    suspend fun doRefreshToken(): LoginResponse{
        val response = authService.doRefreshToken()

        //Check if response code is 200 else it will throw HttpException
        if (response.code() != HttpURLConnection.HTTP_OK) {
            throw HttpException(response)
        }

        //Will automatically throw a NullPointerException when response.body() is Null

        return response.body() ?: throw NullPointerException("Response data is empty")
    }
    suspend fun verifyEmailAuthEnabled() {
        val auth = FirebaseAuth.getInstance()
        try {
            val providers = auth.fetchSignInMethodsForEmail("test@test.com").await()
            Log.d("Auth", "Available providers: $providers")
        } catch (e: Exception) {
            Log.e("Auth", "Error checking providers", e)
        }
    }

}