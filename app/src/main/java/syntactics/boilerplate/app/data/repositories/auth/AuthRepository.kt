package syntactics.boilerplate.app.data.repositories.auth

import com.android.app.data.local.UserLocalData
import com.android.app.data.repositories.auth.response.LoginResponse
import com.android.app.data.repositories.auth.response.UserData
import com.android.app.security.AuthEncryptedDataManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

//class AuthRepository @Inject constructor(
//    private val authRemoteDataSource: AuthRemoteDataSource,
//    private val encryptedDataManager: AuthEncryptedDataManager,
//    private val authLocalDataSource: AuthLocalDataSource,
//    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
//) {
//
//    fun doLogin(email: String, password: String): Flow<LoginResponse> {
//        return flow {
//            val response = authRemoteDataSource.doLogin(email, password)
//            val userInfo = response.data?: UserData()
//            val token = response.token.orEmpty()
//            encryptedDataManager.setAccessToken(token)
//            encryptedDataManager.setUserBasicInfo(userInfo)
//            emit(response)
//        }.flowOn(ioDispatcher)
//    }
//}

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val encryptedDataManager: AuthEncryptedDataManager,
    private val authLocalDataSource: AuthLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun doLogin(email : String, password : String) : Flow<LoginResponse> {
        return flow{
            val response = authRemoteDataSource.doLogin(email, password)
            val userInfo = response.data?: UserData()
            val token = response.token.orEmpty()
            encryptedDataManager.setAccessToken(token)
            authLocalDataSource.login(setUpUserLocalData(userInfo, token))
            emit(response)
        }.flowOn(ioDispatcher)
    }

    fun doRefreshToken() : Flow<LoginResponse> {
        return flow{
            val response = authRemoteDataSource.doRefreshToken()
            val userInfo = response.data?: UserData()
            val token = response.token.orEmpty()
            encryptedDataManager.setAccessToken(token)
            authLocalDataSource.updateToken(userInfo.user_id?:0, token)
            emit(response)
        }.flowOn(ioDispatcher)
    }

    private fun setUpUserLocalData(user : UserData, token : String) : UserLocalData {
        return UserLocalData(
            avatar =user.avatar?.thumb_path,
            email = user.email,
            firstname = user.firstname,
            lastname = user.lastname,
            middlename = user.middlename,
            name = user.name,
            user_id = user.user_id,
            username = user.username,
            access_token = token
        )
    }

    fun getUserInfo() : Flow<UserLocalData>{
        return flow{
            val userLocalData = authLocalDataSource.getUserInfo(encryptedDataManager.getAccessToken())
            emit(userLocalData)
        }.flowOn(ioDispatcher)
    }

    suspend fun doLogout(){
        authLocalDataSource.logout(encryptedDataManager.getAccessToken())
    }
}