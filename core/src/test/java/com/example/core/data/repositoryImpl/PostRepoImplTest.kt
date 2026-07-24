package com.example.core.data.repositoryImpl

import android.content.Context
import android.net.Uri
import app.cash.turbine.test
import com.example.core.data.remote.AuthRemoteDataSource
import com.example.core.data.remote.PostsRemoteDataSource
import com.example.core.data.remote.responses.PostDto
import com.example.core.domain.model.post.Post
import com.example.core.domain.model.post.RequestPost
import com.example.core.domain.model.post.UploadMediaResult
import com.example.core.mapper.ApiMapper
import com.example.core.utils.MediaType
import com.example.core.utils.Response
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class PostRepoImplTest {
    private lateinit var repoImpl: PostRepoImpl

    @Mock
    lateinit var postRemoteDataSource: PostsRemoteDataSource

    @Mock
    lateinit var authRemoteDataSource: AuthRemoteDataSource

    @Mock
    lateinit var listMapper: ApiMapper<List<Post>, List<PostDto>>

    @Mock
    lateinit var detailMapper: ApiMapper<Post, PostDto>

    @Mock
    lateinit var context: Context

    @Before
    fun setup() {
        repoImpl = PostRepoImpl(
            postRemoteDataSource, authRemoteDataSource, listMapper, detailMapper
        )
    }

    @Test
    fun `getPostById should emit Loading then Success`() = runTest {
        val dto = PostDto(
            id = "1",
            userId = "user",
            username = "Asep",
            avatarUrl = "",
            mediaUrl = null,
            mediaType = null,
            description = "Hello",
            createdAt = null,
            updatedAt = null
        )

        val domain = Post(
            id = "1",
            userId = "user",
            username = "Asep",
            avatarUrl = "",
            mediaUrl = null,
            mediaType = null,
            description = "Hello",
            createdAt = null,
            updatedAt = null
        )

        whenever(
            postRemoteDataSource.getPostById("1")
        ).thenReturn(dto)

        whenever(
            detailMapper.mapToDomain(dto)
        ).thenReturn(domain)

        repoImpl.getPostById("1").test {
            val loading = awaitItem()
            assertTrue(loading is Response.Loading)
            val success = awaitItem()
            assertTrue(success is Response.Success)
            assertEquals(
                domain,
                (success as Response.Success).data
            )
            awaitComplete()
        }
    }

    @Test
    fun `getPostById should emit Error`() = runTest {

        whenever(
            postRemoteDataSource.getPostById(any())
        ).thenThrow(RuntimeException("Database Error"))

        repoImpl.getPostById("1").test {
            assert(awaitItem() is Response.Loading)
            val error = awaitItem()
            assertTrue(error is Response.Error)
            assertEquals(
                "Database Error", (error as Response.Error).error?.message
            )
            awaitComplete()
        }
    }

    @Test
    fun `deletePost should emit Success`() = runTest {

        whenever(
            postRemoteDataSource.deletePost("1")
        ).thenReturn(Unit)

        repoImpl.deletePost("1").test {
            assert(awaitItem() is Response.Loading)
            assert(awaitItem() is Response.Success)
            awaitComplete()
        }
    }

    @Test
    fun `deletePost should emit Error`() = runTest {

        doThrow(
            RuntimeException("Delete Failed")
        ).whenever(postRemoteDataSource).deletePost(any())

        repoImpl.deletePost("1").test {
            assert(awaitItem() is Response.Loading)
            val error = awaitItem()
            assertTrue(error is Response.Error)
            awaitComplete()
        }
    }

    @Test
    fun `createPost without media should not upload media`() = runTest {

        val request = RequestPost(
            mediaUri = null, description = "Hello"
        )

        whenever(
            authRemoteDataSource.getCurrentUserId()
        ).thenReturn("user123")

        whenever(
            postRemoteDataSource.createPost(any())
        ).thenReturn(Unit)

        repoImpl.createPost(
            request, context
        ).test {
            assert(awaitItem() is Response.Loading)
            assert(awaitItem() is Response.Success)
            awaitComplete()
        }

        verify(
            postRemoteDataSource, never()
        ).uploadMedia(
            any(), any()
        )

    }

    @Test
    fun `createPost with media should not upload media`() = runTest {

        val request = RequestPost(
            mediaUri = null, description = "Hello"
        )

        whenever(
            authRemoteDataSource.getCurrentUserId()
        ).thenReturn("user123")

        whenever(
            postRemoteDataSource.createPost(any())
        ).thenReturn(Unit)

        repoImpl.createPost(
            request, context
        ).test {
            assert(awaitItem() is Response.Loading)
            assert(awaitItem() is Response.Success)
            awaitComplete()
        }

        verify(
            postRemoteDataSource, never()
        ).uploadMedia(
            any(), any()
        )
    }

    @Test
    fun `updatePost with media should not upload media`() = runTest {

        val request = RequestPost(
            mediaUri = null, description = "Hello"
        )

        whenever(
            authRemoteDataSource.getCurrentUserId()
        ).thenReturn("user123")

        whenever(
            authRemoteDataSource.getCurrentUserId()
        ).thenReturn("user123")

        repoImpl.updatePost(
            postId = "post123", context = context, data = request
        ).test {
            assert(awaitItem() is Response.Loading)
            assert(awaitItem() is Response.Success)
            awaitComplete()
        }

        verify(
            postRemoteDataSource, never()
        ).uploadMedia(
            any(), any()
        )

    }

    @Test
    fun `updatePost without new media should only update database`() = runTest {
        // Arrange
        whenever(
            authRemoteDataSource.getCurrentUserId()
        ).thenReturn("user123")

        val request = RequestPost(
            mediaUri = null,
            oldMediaUrl = "https://old-image.jpg",
            oldMediaType = MediaType.IMAGE,
            description = "Updated Description"
        )

        whenever(
            postRemoteDataSource.updatePost(
                any(),
                any()
            )
        ).thenReturn(Unit)

        // Act
        val result = repoImpl.updatePost(
            postId = "post123", context = context, data = request
        ).toList()

        // Assert
        assertTrue(result[0] is Response.Loading)
        assertTrue(result[1] is Response.Success)

        verify(postRemoteDataSource).updatePost(
            eq("post123"), any()
        )

        verify(postRemoteDataSource, never()).uploadMedia(any(), any())

        verify(postRemoteDataSource, never()).deleteMedia(any())
    }

    @Test
    fun `updatePost with new media should upload update and delete old media`() = runTest {
        // Arrange
        val uri = mock<Uri>()

        whenever(authRemoteDataSource.getCurrentUserId())
            .thenReturn("user123")

        whenever(
            postRemoteDataSource.uploadMedia(
                uri,
                context
            )
        ).thenReturn(
            UploadMediaResult(
                mediaUrl = "https://new-image.jpg",
                mediaType = "IMAGE"
            )
        )

        whenever(
            postRemoteDataSource.updatePost(
                eq("post123"),
                any()
            )
        ).thenReturn(Unit)

        whenever(
            postRemoteDataSource.deleteMedia(
                any()
            )
        ).thenReturn(Unit)

        val request = RequestPost(
            mediaUri = uri,
            oldMediaUrl = "https://old-image.jpg",
            oldMediaType = MediaType.IMAGE,
            description = "Updated Description"
        )

        // Act
        val result = repoImpl.updatePost(
            postId = "post123",
            context = context,
            data = request
        ).toList()

        // Assert
        assertEquals(2, result.size)
        assertTrue(result[0] is Response.Loading)
        assertTrue(result[1] is Response.Success)

        verify(postRemoteDataSource).uploadMedia(
            uri,
            context
        )

        verify(postRemoteDataSource).updatePost(
            eq("post123"),
            any()
        )

        verify(postRemoteDataSource).deleteMedia(
            "https://old-image.jpg"
        )
    }
}