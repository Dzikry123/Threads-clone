package com.example.core.domain.usecases.posts

import android.content.Context
import androidx.paging.PagingData
import com.example.core.domain.model.post.Post
import com.example.core.domain.model.post.RequestPost
import com.example.core.domain.repository.IPostsRepository
import com.example.core.utils.MediaType
import com.example.core.utils.Response
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class PostsInteractorTest {

    @Mock
    lateinit var repository: IPostsRepository

    @Mock
    lateinit var context: Context

    private lateinit var interactor: PostsInteractor

    @Before
    fun setup() {
        interactor = PostsInteractor(repository)
    }

    @Test
    fun `get data from repository should call from the usecase`() = runTest {
        val flow = flowOf(PagingData.empty<Post>())

        whenever(
            repository.getPosts()
        ).thenReturn(flow)

        assertEquals(flow, interactor.getPosts())
        verify(repository).getPosts()
    }

    @Test
    fun `get data from repository by id should call from usecase`() = runTest {
        val flow = flowOf(PagingData.empty<Post>())

        whenever(
            repository.getPostsByUserId("user123")
        ).thenReturn(flow)

        interactor.getPostsByUserId("user123")

        verify(repository).getPostsByUserId("user123")
    }

    @Test
    fun `get data from repository for detail post`() = runTest {
        val flow = flowOf<Response<Post>>(
            Response.Success(
                Post(
                    id = "1",
                    userId = "user123",
                    username = "asep",
                    avatarUrl = "https://image.jpg",
                    mediaUrl = "https://image.jpg",
                    mediaType = MediaType.IMAGE,
                    description = "hello",
                    createdAt = "2025-01-02",
                    updatedAt = "2025-01-03"
                )
            )
        )

        whenever(
            repository.getPostById("1")
        ).thenReturn(flow)

        assertEquals(
            flow,
            interactor.getPostById("1")
        )

        verify(repository).getPostById("1")
    }

    @Test
    fun `should call createPost from repository via useCase`() = runTest {
        val request = RequestPost(
            mediaUri = null,
            oldMediaUrl = null,
            oldMediaType = null,
            description = "hello"
        )

        val flow = flowOf<Response<Unit>>(
            Response.Success(Unit)
        )

        whenever(
            repository.createPost(request, context)
        ).thenReturn(flow)

        assertEquals(
            flow,
            interactor.createPost(request, context)
        )
        verify(repository).createPost(request, context)
    }

    @Test
    fun `should call update method from repository via usecase`() = runTest {
        val request = RequestPost(
            mediaUri = null,
            oldMediaUrl = null,
            oldMediaType = null,
            description = "hello"
        )

        val flow = flowOf<Response<Unit>>(
            Response.Success(Unit)
        )

        whenever(
            repository.updatePost("1",context, request)
        ).thenReturn(flow)

        assertEquals(
            flow,
            interactor.updatePost("1", context, request)
        )

        verify(repository).updatePost("1", context, request)
    }

    @Test
    fun `should call delete method from repository via usecase`() = runTest {
        val flow = flowOf<Response<Unit>>(
            Response.Success(Unit)
        )

        whenever(
            repository.deletePost("1")
        ).thenReturn(flow)

        assertEquals(
            flow,
            interactor.deletePost("1")
        )

        verify(repository).deletePost("1")
    }
}