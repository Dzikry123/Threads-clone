package com.example.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.core.data.remote.PostsRemoteDataSource
import com.example.core.data.remote.responses.PostDto
import com.example.core.domain.model.post.Post
import com.example.core.mapper.ApiMapper

class PostsPagingSource(
    private val remoteDataSource: PostsRemoteDataSource,
    private val mapper: ApiMapper<List<Post>, List<PostDto>>,
    private val userId: String? = null
) : PagingSource<Int, Post>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Post> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize

            val from = page * pageSize
            val to = from + pageSize - 1

            val dto = if (userId == null) {
                remoteDataSource.getPosts(
                    from = from.toLong(),
                    to = to.toLong()
                )
            } else {
                remoteDataSource.getPostsByUserId(userId, from = from.toLong(),
                    to = to.toLong())
            }

            val posts = mapper.mapToDomain(dto)

            LoadResult.Page(
                data = posts,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (posts.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(
        state: PagingState<Int, Post>
    ): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}