package com.example.core.domain.usecases.profile

import android.content.Context
import com.example.core.domain.model.profile.Profile
import com.example.core.domain.model.profile.UpdateProfile
import com.example.core.utils.Response
import kotlinx.coroutines.flow.Flow

interface ProfileUseCases {
    suspend fun getProfileUser(): Flow<Response<Profile>>
    suspend fun updateProfileUser(profile: UpdateProfile, context: Context): Flow<Response<Unit>>
}