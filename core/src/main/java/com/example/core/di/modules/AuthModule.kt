package com.example.core.di.modules

import com.example.core.data.remote.AuthRemoteDataSource
import com.example.core.data.repositoryImpl.AuthRepoImpl
import com.example.core.domain.repository.IAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        remoteDataSource: AuthRemoteDataSource
    ): IAuthRepository {
        return AuthRepoImpl(remoteDataSource)
    }
}