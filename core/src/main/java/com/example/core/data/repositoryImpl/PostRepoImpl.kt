package com.example.core.data.repositoryImpl

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.core.data.paging.PostsPagingSource
import com.example.core.data.remote.AuthRemoteDataSource
import com.example.core.data.remote.PostsRemoteDataSource
import com.example.core.data.remote.responses.PostDto
import com.example.core.data.remote.responses.RequestPostDto
import com.example.core.domain.model.post.Post
import com.example.core.domain.model.post.RequestPost
import com.example.core.domain.repository.IPostsRepository
import com.example.core.mapper.ApiMapper
import com.example.core.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostRepoImpl @Inject constructor(
    private val remoteDataSource: PostsRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val getPostApiMapper: ApiMapper<List<Post>, List<PostDto>>,
    private val getPostDetailApiMapper: ApiMapper<Post, PostDto>
) : IPostsRepository {

    override fun getPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PostsPagingSource(
                    remoteDataSource,
                    getPostApiMapper
                )
            }

        ).flow
    }

    override fun getPostsByUserId(userId: String): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PostsPagingSource(
                    userId = userId,
                    remoteDataSource = remoteDataSource,
                    mapper = getPostApiMapper
                )
            }
        ).flow
    }

    override suspend fun getPostById(postId: String): Flow<Response<Post>> = flow {
        emit(Response.Loading())
        try {
            val apiDto = remoteDataSource.getPostById(postId)
            getPostDetailApiMapper.mapToDomain(apiDto).apply {
                emit(Response.Success(this))
            }
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }

    override suspend fun createPost(data: RequestPost, context: Context): Flow<Response<Unit>> =
        flow {
            emit(Response.Loading())
            try {
                var mediaUrl: String? = null
                var mediaType: String? = null
                data.mediaUri?.let { uri ->

                    val uploadResult =
                        remoteDataSource.uploadMedia(
                            uri,
                            context
                        )

                    mediaUrl = uploadResult.mediaUrl
                    mediaType = uploadResult.mediaType
                }


                val result = remoteDataSource.createPost(
                    RequestPostDto(
                        userId = authRemoteDataSource.getCurrentUserId() ?: "User not logged in",
                        mediaUrl = mediaUrl,
                        mediaType = mediaType,
                        description = data.description
                    )
                )
                emit(Response.Success(result))
            } catch (e: Exception) {
                emit(Response.Error(e))
                Log.e(
                    "CREATE_POST_ERROR",
                    "msg=${e.message}",
                    e
                )
            }
        }

    override suspend fun updatePost(
        postId: String,
        context: Context,
        data: RequestPost
    ): Flow<Response<Unit>> = flow {

        emit(Response.Loading())

        try {

            var mediaUrl = data.oldMediaUrl
            var mediaType = data.oldMediaType?.name

            var uploadedNewMedia = false

            if (data.mediaUri != null) {

                val uploadResult = remoteDataSource.uploadMedia(
                    uri = data.mediaUri,
                    context = context
                )

                mediaUrl = uploadResult.mediaUrl
                mediaType = uploadResult.mediaType
                uploadedNewMedia = true
            }

            // update database dulu
            remoteDataSource.updatePost(
                postId = postId,
                data = RequestPostDto(
                    userId = authRemoteDataSource.getCurrentUserId()
                        ?: error("User not logged in"),
                    mediaUrl = mediaUrl,
                    mediaType = mediaType,
                    description = data.description
                )
            )

            // baru hapus media lama
            if (uploadedNewMedia) {
                data.oldMediaUrl?.let {
                    remoteDataSource.deleteMedia(it)
                }
            }

            emit(Response.Success(Unit))

        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }

    override suspend fun deletePost(postId: String): Flow<Response<Unit>> = flow {
        emit(Response.Loading())
        try {
            val result = remoteDataSource.deletePost(postId)
            emit(Response.Success(result))
        } catch (e: Exception) {
            emit(Response.Error(e))
        }
    }
}