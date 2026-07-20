package com.example.core.domain.usecases.posts

import android.content.Context
import androidx.paging.PagingData
import com.example.core.domain.model.post.Post
import com.example.core.domain.model.post.RequestPost
import com.example.core.domain.repository.IPostsRepository
import com.example.core.utils.Response
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostsInteractor @Inject constructor(
    private val repository: IPostsRepository
): PostsUseCases{
    override fun getPosts(): Flow<PagingData<Post>> {
        return repository.getPosts()
    }

    override fun getPostsByUserId(postId: String): Flow<PagingData<Post>> {
        return repository.getPostsByUserId(postId)
    }

    override suspend fun getPostById(postId: String): Flow<Response<Post>> {
        return repository.getPostById(postId)
    }

    override suspend fun createPost(data: RequestPost, context: Context): Flow<Response<Unit>> {
        return repository.createPost(data, context)
    }

    override suspend fun updatePost(postId: String,  context: Context, data: RequestPost): Flow<Response<Unit>> {
        return repository.updatePost(postId, context, data )
    }

    override suspend fun deletePost(postId: String): Flow<Response<Unit>> {
        return repository.deletePost(postId)
    }

}