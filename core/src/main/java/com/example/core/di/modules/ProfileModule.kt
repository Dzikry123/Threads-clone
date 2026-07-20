package com.example.core.di.modules

import com.example.core.data.remote.AuthRemoteDataSource
import com.example.core.data.remote.ProfileRemoteDataSource
import com.example.core.data.remote.responses.ProfileDto
import com.example.core.data.repositoryImpl.ProfileRepoImpl
import com.example.core.domain.model.profile.Profile
import com.example.core.domain.repository.IProfileRepository
import com.example.core.mapper.ApiMapper
import com.example.core.mapper.profile.ProfileApiMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileRepository(
        remoteDataSource: ProfileRemoteDataSource,
        authRemoteDataSource: AuthRemoteDataSource,
        apiMapper: ApiMapper<Profile, ProfileDto>
    ): IProfileRepository {
        return ProfileRepoImpl(remoteDataSource, authRemoteDataSource, apiMapper)
    }

    @Provides
    @Singleton
    fun provideApiMapper(): ApiMapper<Profile, ProfileDto> = ProfileApiMapperImpl()
}