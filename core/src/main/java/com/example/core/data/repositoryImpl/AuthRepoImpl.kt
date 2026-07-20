package com.example.core.data.repositoryImpl

import android.util.Log
import com.example.core.data.remote.AuthRemoteDataSource
import com.example.core.domain.model.auth.AuthUser
import com.example.core.domain.repository.IAuthRepository
import com.example.core.utils.Response
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource
) : IAuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Flow<Response<AuthUser>> = flow {
        emit(Response.Loading())
        try {
            remoteDataSource.login(email, password)
            Log.d(
                "AUTH STATE SESSION TOKEN",
                "${remoteDataSource.getCurrentSession()}"
            )
            val userData = remoteDataSource.getCurrentUser()
            userData?.let { data ->
                emit(
                    Response.Success(
                        AuthUser(
                            data.id,
                            data.email
                        )
                    )
                )
            }
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Flow<Response<AuthUser>> = flow {
        emit(Response.Loading())
        try {
            remoteDataSource.signInWithGoogle(idToken)
            Log.d(
                "AUTH STATE SESSION TOKEN",
                "${remoteDataSource.getCurrentSession()}"
            )
            val userData = remoteDataSource.getCurrentUser()
            userData?.let { data ->
                emit(
                    Response.Success(
                        AuthUser(
                            data.id,
                            data.email
                        )
                    )
                )
            }
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }

    override suspend fun register(
        email: String,
        password: String
    ): Flow<Response<AuthUser>> = flow {
        emit(Response.Loading())
        try {
            remoteDataSource.register(email, password)
            val userData = remoteDataSource.getCurrentUser()
            userData?.let { data ->
                emit(
                    Response.Success(
                        AuthUser(
                            data.id,
                            data.email
                        )
                    )
                )
            }
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }

    override suspend fun logout() {
        return remoteDataSource.logout()
    }

    override suspend fun isLoggedIn(): Boolean {

        repeat(10) {

            val session =
                remoteDataSource.getCurrentSession()

            val user =
                remoteDataSource.getCurrentUser()

            Log.d(
                "AUTH_CHECK",
                "session=$session"
            )

            Log.d(
                "AUTH_CHECK",
                "user=$user"
            )

            if (session != null) {
                return true
            }

            delay(1000)
        }

        return false
    }

    override fun checkIdUser(): String? {
        return remoteDataSource.getCurrentUserId()
    }

}