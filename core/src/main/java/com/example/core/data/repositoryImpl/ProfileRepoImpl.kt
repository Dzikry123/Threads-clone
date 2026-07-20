package com.example.core.data.repositoryImpl

import android.content.Context
import com.example.core.data.remote.AuthRemoteDataSource
import com.example.core.data.remote.ProfileRemoteDataSource
import com.example.core.data.remote.responses.ProfileDto
import com.example.core.domain.model.profile.Profile
import com.example.core.domain.model.profile.UpdateProfile
import com.example.core.domain.repository.IProfileRepository
import com.example.core.mapper.ApiMapper
import com.example.core.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProfileRepoImpl @Inject constructor(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val apiMapper: ApiMapper<Profile, ProfileDto>
) : IProfileRepository {
    override suspend fun getProfileUser(): Flow<Response<Profile>> = flow {
        emit(Response.Loading())
        try {
            val profileData = remoteDataSource.getProfileUser()
            apiMapper.mapToDomain(profileData).apply {
                emit(Response.Success(this))
            }
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }

    override suspend fun updateProfileUser(profile: UpdateProfile, context: Context): Flow<Response<Unit>> = flow {
        emit(Response.Loading())
        try {
            var avatarUrl = profile.avatarUrl

            var uploadedNewMedia = false
            if (profile.mediaUri != null) {
                val uploadResult = remoteDataSource.uploadMedia(
                    uri = profile.mediaUri,
                    context = context
                )
                avatarUrl = uploadResult.mediaUrl
                uploadedNewMedia = true
            }

            remoteDataSource.updateProfileUser(
                request = UpdateProfile(
                    id = authRemoteDataSource.getCurrentUserId()
                        ?: error("User not logged in"),
                    avatarUrl = avatarUrl,
                    username = profile.username,
                    fullName = profile.fullName,
                    bio = profile.bio,
                )
            )
            if (uploadedNewMedia) {
                profile.avatarUrl?.let {
                    remoteDataSource.deleteMedia(it)
                }
            }
            emit(Response.Success(Unit))
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }

}