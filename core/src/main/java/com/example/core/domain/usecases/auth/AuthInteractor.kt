package com.example.core.domain.usecases.auth

import com.example.core.domain.model.auth.AuthUser
import com.example.core.domain.repository.IAuthRepository
import com.example.core.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthInteractor @Inject constructor(
    private val repository: IAuthRepository
) : AuthUseCases {
    override suspend fun login(
        email: String,
        password: String
    ): Flow<Response<AuthUser>> {
        return repository.login(email, password)
    }

    override suspend fun loginWithGoogle(idToken: String): Flow<Response<AuthUser>> {
        return repository.loginWithGoogle(idToken)
    }

    override suspend fun register(
        email: String,
        password: String
    ): Flow<Response<AuthUser>> {
        return repository.register(email, password)
    }

    override suspend fun logout() {
        return repository.logout()
    }

    override suspend fun isLoggedIn(): Boolean {
        return repository.isLoggedIn()
    }

    override fun checkIdUser(): String? {
        return repository.checkIdUser()
    }
}