package com.example.socialapp.di

import com.example.core.domain.usecases.auth.AuthInteractor
import com.example.core.domain.usecases.auth.AuthUseCases
import com.example.core.domain.usecases.posts.PostsInteractor
import com.example.core.domain.usecases.posts.PostsUseCases
import com.example.core.domain.usecases.profile.ProfileInteractor
import com.example.core.domain.usecases.profile.ProfileUseCases
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun provideAuthUseCases(
        authInteractor: AuthInteractor
    ): AuthUseCases

    @Binds
    @Singleton
    abstract fun provideProfileUseCases(
        profileInteractor: ProfileInteractor
    ): ProfileUseCases

    @Binds
    @Singleton
    abstract fun providePostsUseCases(
        postsInteractor: PostsInteractor
    ): PostsUseCases
}