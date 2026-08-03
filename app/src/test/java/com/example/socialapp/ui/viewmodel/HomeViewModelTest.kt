package com.example.socialapp.ui.viewmodel

import android.content.Context
import androidx.paging.PagingData
import app.cash.turbine.test
import com.example.core.domain.model.post.Post
import com.example.core.domain.model.post.RequestPost
import com.example.core.domain.usecases.posts.PostsUseCases
import com.example.core.utils.MediaType
import com.example.core.utils.Response
import com.example.socialapp.ui.navigation.NavigateEvent
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var useCases: PostsUseCases

    @Mock
    lateinit var context: Context

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        whenever(
            useCases.getPosts()
        ).thenReturn(flowOf(PagingData.empty()))
        viewModel = HomeViewModel(useCases)
    }

    @Test
    fun `posts should come from usecase`() = runTest {
        val pagingData = PagingData
            .from(
                listOf(
                    Post(
                        id = "1",
                        userId = "user123",
                        username = "asep",
                        avatarUrl = "TODO()",
                        mediaUrl = "https://image.jpg",
                        mediaType = MediaType.IMAGE,
                        description = "hallo",
                        createdAt = "2025-01-02",
                        updatedAt = "2025-01-03"
                    )
                )
            )

        whenever(
            useCases.getPosts()
        ).thenReturn(flowOf(pagingData))

        viewModel = HomeViewModel(useCases)
        val result = viewModel.posts.first()

        assertNotNull(result)
    }

    @Test
    fun `create post should update state to success` () = runTest {
        val request = RequestPost(
            mediaUri = null,
            oldMediaUrl = null,
            oldMediaType = null,
            description = "hello"
        )

        whenever(
            useCases.createPost(
                request,
                context
            )
        ).thenReturn(
            flow {
                emit(Response.Loading())
                emit(Response.Success(Unit))
            }
        )

        viewModel.createPost(
            request,
            context
        )

        advanceUntilIdle()
        val state = viewModel.postState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `create post should update state error`() = runTest {
        val request = RequestPost(
            mediaUri = null,
            oldMediaUrl = null,
            oldMediaType = null,
            description = "null"
        )

        whenever(
            useCases.createPost(request, context)
        ).thenReturn(
            flow {
                emit(Response.Loading())
                emit(Response.Error(Exception("Create Failed")))
            }
        )

        viewModel.createPost(
            request, context
        )
        advanceUntilIdle()
        val state = viewModel.postState.value
        assertFalse(state.isLoading)
        assertEquals(
            null,
            state.error
        )
    }

    @Test
    fun `create post should navigate pop up user to homepage`() = runTest {
        val request = RequestPost(
            mediaUri = null,
            oldMediaUrl = null,
            oldMediaType = null,
            description = "navigate"
        )

        whenever(
            useCases.createPost(request, context )
        ).thenReturn(
            flow {
                emit(Response.Loading())
                emit(Response.Success(Unit))
            }
        )

        viewModel.event.test {
            viewModel.createPost(request, context)
            assertEquals(
                NavigateEvent.PopUp,
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `create post should set loading true while loading`() = runTest {
        val request = RequestPost(
            mediaUri = null,
            oldMediaUrl = null,
            oldMediaType = null,
            description = "null"
        )

        whenever(
            useCases.createPost(request, context)
        ).thenReturn(
            flow {
                emit(Response.Loading())
            }
        )

        viewModel.postState.test {
            assertEquals(false, awaitItem().isLoading)
            viewModel.createPost(request, context)
            assertTrue(awaitItem().isLoading)
            cancelAndIgnoreRemainingEvents()
        }

    }
}


class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description?) {
        super.starting(description)
    }
    override fun finished(description: Description?) {
        super.finished(description)
    }
}