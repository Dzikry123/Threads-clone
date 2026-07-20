package com.example.core.di.modules

import com.example.core.data.remote.AuthRemoteDataSource
import com.example.core.data.remote.PostsRemoteDataSource
import com.example.core.data.remote.responses.PostDto
import com.example.core.data.repositoryImpl.PostRepoImpl
import com.example.core.domain.model.post.Post
import com.example.core.domain.repository.IPostsRepository
import com.example.core.mapper.ApiMapper
import com.example.core.mapper.post.GetPostApiMapperImpl
import com.example.core.mapper.post.GetPostDetailApiMapperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostsModule {

    @Provides
    @Singleton
    fun providePostRepository(
        remoteDataSource: PostsRemoteDataSource,
        authRemoteDataSource: AuthRemoteDataSource,
        getPostApiMapper: ApiMapper<List<Post>, List<PostDto>>,
        @GetPostDetailMapper
        getPostDetailApiMapper: ApiMapper<Post, PostDto>,

    ): IPostsRepository {
        return PostRepoImpl(remoteDataSource,authRemoteDataSource, getPostApiMapper, getPostDetailApiMapper)
    }

    @Provides
    @Singleton
    fun provideGetPostApiMapper(): ApiMapper<List<Post>, List<PostDto>> = GetPostApiMapperImpl()

    @Provides
    @Singleton
    @GetPostDetailMapper
    fun provideGetPostDetailApiMapper(): ApiMapper<Post, PostDto> = GetPostDetailApiMapperImpl()

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GetPostDetailMapper
}