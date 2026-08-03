package com.example.core.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.core.data.remote.PostsRemoteDataSource
import com.example.core.data.remote.responses.PostDto
import com.example.core.domain.model.post.Post
import com.example.core.mapper.ApiMapper
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class PostsPagingSourceTest {
    @Mock
    lateinit var remoteDataSource: PostsRemoteDataSource

    @Mock
    lateinit var mapper: ApiMapper<List<Post>, List<PostDto>>

    private lateinit var pagingSource: PostsPagingSource

    @Before
    fun setup() {
        pagingSource = PostsPagingSource(
            remoteDataSource = remoteDataSource,
            mapper = mapper,
        )
    }

    @Test
    fun `load should return Page when remote success`() = runTest {
        PostsPagingSource(
            remoteDataSource = remoteDataSource,
            mapper = mapper,
        )

        val dto = listOf(
            PostDto(
                id = "1",
                userId = "user1",
                username = "asep",
                avatarUrl = "",
                mediaUrl = null,
                mediaType = null,
                description = "hello",
                createdAt = null,
                updatedAt = null
            )
        )

        val domain = listOf(
            Post(
                id = "1",
                userId = "user1",
                username = "asep",
                avatarUrl = "",
                mediaUrl = null,
                mediaType = null,
                description = "hello",
                createdAt = null,
                updatedAt = null
            )
        )

        whenever(
            remoteDataSource.getPosts(0, 9)
        ).thenReturn(dto)

        whenever(
            mapper.mapToDomain(dto)
        ).thenReturn(domain)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)

        result as PagingSource.LoadResult.Page
        assertEquals(domain, result.data)
        assertNull(result.prevKey)
        assertEquals(1, result.nextKey)
    }

    @Test
    fun `load should call getPostsById when userId exists`() = runTest {
        pagingSource = PostsPagingSource(
            remoteDataSource = remoteDataSource,
            mapper = mapper,
            userId = "user123"
        )

        val dto = emptyList<PostDto>()

        whenever(
            remoteDataSource.getPostsByUserId(
                userId = "user123",
                from = 0,
                to = 9
            )
        ).thenReturn(dto)

        whenever(
            mapper.mapToDomain(dto)
        ).thenReturn(emptyList())

        pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        verify(remoteDataSource)
            .getPostsByUserId(
                "user123",
                0,
                9
            )
    }

    @Test
    fun `load should return error when remote throws exception`() = runTest {
        pagingSource = PostsPagingSource(
            remoteDataSource = remoteDataSource,
            mapper = mapper,
        )

        whenever(
            remoteDataSource.getPosts(
                anyLong(),
                anyLong()
            )
        ).thenThrow(
            RuntimeException("Database Error")
        )

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        result as PagingSource.LoadResult.Error

        assertEquals(
            "Database Error",
            result.throwable.message
        )
    }


    @Test
    fun `load should return null nextKey when its empty`() = runTest {
        pagingSource = PostsPagingSource(
            remoteDataSource = remoteDataSource,
            mapper = mapper,
        )

        whenever(
            remoteDataSource.getPosts(
                0,
                9
            )
        ).thenReturn(emptyList())

        whenever(
            mapper.mapToDomain(emptyList())
        ).thenReturn(emptyList())

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        result as PagingSource.LoadResult.Page
        assertNull(result.nextKey)
    }

    @Test
    fun `getRefreshKey should return correct key`() {
        pagingSource = PostsPagingSource(
            remoteDataSource,
            mapper,
        )

        val dummyPost = Post(
            id = "1",
            userId = "123",
            username = "asep",
            avatarUrl = "",
            mediaUrl = null,
            mediaType = null,
            description = "hello",
            createdAt = null,
            updatedAt = null
        )

        val page = PagingSource.LoadResult.Page(
            data = listOf(dummyPost),
            prevKey = null,
            nextKey = 1
        )

        val state = PagingState(
            pages = listOf(page),
            anchorPosition = 1,
            config = PagingConfig(10),
            leadingPlaceholderCount = 0
        )

        val key = pagingSource.getRefreshKey(state)
        assertEquals(0, key)
    }
}