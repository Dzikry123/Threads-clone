package com.example.core.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val supabase: SupabaseClient
) {
    suspend fun login(
        email: String,
        password: String
    ) {
        supabase.auth.signInWith(Email){
            this.email = email
            this.password = password
        }
    }

    suspend fun register(
        email: String,
        password: String
    ) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signInWithGoogle(
        idToken: String
    ) {
        supabase.auth.signInWith(IDToken) {
            provider = Google
            this.idToken = idToken
        }
    }

    suspend fun logout() {
        supabase.auth.signOut()
    }

    fun getCurrentSession() =
        supabase.auth.currentSessionOrNull()

    fun getCurrentUser() =
        supabase.auth.currentUserOrNull()

    fun getCurrentUserId() =
        supabase.auth.currentUserOrNull()?.id
}