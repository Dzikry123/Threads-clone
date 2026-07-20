package com.example.core.domain.usecases.posts

import android.content.Context
import androidx.paging.PagingData
import com.example.core.domain.model.post.Post
import com.example.core.domain.model.post.RequestPost
import com.example.core.utils.Response
import kotlinx.coroutines.flow.Flow

interface PostsUseCases {
    fun getPosts(): Flow<PagingData<Post>>
    fun getPostsByUserId(postId: String): Flow<PagingData<Post>>
    suspend fun getPostById(postId: String): Flow<Response<Post>>
    suspend fun createPost(data: RequestPost, context: Context): Flow<Response<Unit>>
    suspend fun updatePost(postId: String, context: Context, data: RequestPost): Flow<Response<Unit>>
    suspend fun deletePost(postId: String): Flow<Response<Unit>>
}