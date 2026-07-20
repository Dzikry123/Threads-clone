package com.example.core.domain.repository

import com.example.core.domain.model.auth.AuthUser
import com.example.core.utils.Response
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    suspend fun login(email: String, password: String): Flow<Response<AuthUser>>
    suspend fun loginWithGoogle(idToken: String): Flow<Response<AuthUser>>
    suspend fun register(email: String, password: String): Flow<Response<AuthUser>>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
    fun checkIdUser(): String?
}