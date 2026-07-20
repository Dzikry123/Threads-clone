package com.example.core.domain.usecases.profile

import android.content.Context
import com.example.core.domain.model.profile.Profile
import com.example.core.domain.model.profile.UpdateProfile
import com.example.core.domain.repository.IProfileRepository
import com.example.core.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileInteractor @Inject constructor(
    private val repository: IProfileRepository
): ProfileUseCases {
    override suspend fun getProfileUser(): Flow<Response<Profile>> {
        return repository.getProfileUser()
    }

    override suspend fun updateProfileUser(profile: UpdateProfile, context: Context): Flow<Response<Unit>> {
        return repository.updateProfileUser(profile, context)
    }
}