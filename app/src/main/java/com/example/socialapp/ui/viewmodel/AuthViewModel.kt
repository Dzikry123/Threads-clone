package com.example.socialapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.auth.AuthUser
import com.example.core.domain.usecases.auth.AuthUseCases
import com.example.core.utils.handleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val useCases: AuthUseCases,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState = _authState.asStateFlow()

    // Text Input
    val emailInput: StateFlow<String> = savedStateHandle.getStateFlow("email", "")
    fun updateEmailInput(input: String) {
        savedStateHandle["email"] = input
    }

    val passwordInput: StateFlow<String> = savedStateHandle.getStateFlow("password", "")
    fun updatePasswordInput(input: String) {
        savedStateHandle["password"] = input
    }

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            _authState.value =
                if (useCases.isLoggedIn()) {
                    Log.d(
                        "AUTH Session State Status",
                        "Status=${_authState.value}"
                    )
                    Log.d(
                        "AUTH Session Is Log In",
                        "Status=${useCases.isLoggedIn()}"
                    )
                    AuthState.Authenticated
                } else {
                    Log.d(
                        "AUTH Session State Status",
                        "Status=${_authState.value}"
                    )
                    AuthState.Unauthenticated
                }

            Log.d(
                "AUTH Session State Status",
                "Status=${_authState.value}"
            )
        }
    }

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            useCases.login(email, password).handleResponse(
                onError = { error ->
                    _authState.value = AuthState.Unauthenticated
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error?.message
                        )
                    }
                },
                onLoading = {
                    _authState.value = AuthState.Loading
                    _uiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
            ) { authUser ->
                _uiState.update {
                    _authState.value = AuthState.Authenticated
                    it.copy(
                        isLoading = false,
                        error = null,
                        isSuccess = true,
                        user = authUser
                    )
                }
            }

        }
    }

     fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            useCases.loginWithGoogle(idToken).handleResponse(
                onError = { error ->
                    _uiState.update {
                        _authState.value = AuthState.Unauthenticated
                        it.copy(
                            isLoading = false,
                            error = error?.message
                        )
                    }
                },
                onLoading = {
                    _authState.value = AuthState.Loading
                    _uiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
            ) { authUser ->
                _authState.value = AuthState.Authenticated
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        isSuccess = true,
                        user = authUser
                    )
                }
            }
        }
    }

     fun register(email: String, password: String) {
        viewModelScope.launch {
            useCases.register(email, password).handleResponse(
                onError = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error?.message
                        )
                    }
                },
                onLoading = {
                    _uiState.update {
                        it.copy(
                            isLoading = true
                        )
                    }
                }
            ) { authUser ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        isSuccess = true,
                    )
                }
            }
        }
    }

     fun logout() {
        viewModelScope.launch {
            useCases.logout()
            _authState.value = AuthState.Unauthenticated
        }
    }
}

data class LoginUiState(
    val user: AuthUser? = null,

    val error: String? = null,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface AuthState {
    data object Loading: AuthState
    data object Authenticated: AuthState
    data object Unauthenticated: AuthState
}