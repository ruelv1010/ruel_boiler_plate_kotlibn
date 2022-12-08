package com.android.boilerplate.ui.sample.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.boilerplate.data.model.ErrorModel
import com.android.boilerplate.data.repositories.auth.AuthRepository
import com.android.boilerplate.utils.PopupErrorState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginSharedFlow = MutableSharedFlow<LoginViewState>()

    val loginSharedFlow: SharedFlow<LoginViewState> =
        _loginSharedFlow.asSharedFlow()


    fun doLoginAccount(email: String, password: String) {
        viewModelScope.launch {
            authRepository.doLogin(email, password)
                .onStart {
                    _loginSharedFlow.emit(LoginViewState.Loading)
                }
                .catch { exception ->
                    onError(exception)

                }
                .collect {
                    _loginSharedFlow.emit(
                        LoginViewState.Success(it.msg.orEmpty())
                    )
                }
        }
    }


    private suspend fun onError(exception: Throwable) {
        when (exception) {
            is IOException,
            is TimeoutException,
            -> {
                _loginSharedFlow.emit(
                    LoginViewState.PopupError(
                        PopupErrorState.NetworkError
                    )
                )
            }
            is HttpException -> {
                val errorBody = exception.response()?.errorBody()
                val gson = Gson()
                val type = object : TypeToken<ErrorModel>() {}.type
                var errorResponse: ErrorModel? = gson.fromJson(errorBody?.charStream(), type)
                if (errorResponse?.has_requirements == true) {
                    _loginSharedFlow.emit(LoginViewState.InputError(errorResponse.errors))
                } else {
                    _loginSharedFlow.emit(
                        LoginViewState.PopupError(
                            PopupErrorState.HttpError, errorResponse?.msg.orEmpty()
                        )
                    )
                }

            }
            else -> _loginSharedFlow.emit(
                LoginViewState.PopupError(
                    PopupErrorState.UnknownError
                )
            )
        }
    }

}