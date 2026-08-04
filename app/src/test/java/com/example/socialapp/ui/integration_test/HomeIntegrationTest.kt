package com.example.socialapp.ui.integration_test

import android.content.Context
import android.net.Uri
import com.example.core.data.remote.AuthRemoteDataSource
import com.example.core.data.remote.PostsRemoteDataSource
import com.example.core.data.remote.responses.PostDto
import com.example.core.data.repositoryImpl.PostRepoImpl
import com.example.core.domain.model.post.Post
import com.example.core.domain.model.post.RequestPost
import com.example.core.domain.model.post.UploadMediaResult
import com.example.core.domain.repository.IPostsRepository
import com.example.core.domain.usecases.posts.PostsInteractor
import com.example.core.domain.usecases.posts.PostsUseCases
import com.example.core.mapper.ApiMapper
import com.example.socialapp.ui.viewmodel.HomeViewModel
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.jvm.java
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(MockitoJUnitRunner::class)
class HomeIntegrationTest {
    @Mock
    lateinit var postRemoteDatasource: PostsRemoteDataSource

    @Mock
    lateinit var authRemoteDataSource: AuthRemoteDataSource

    @Mock
    lateinit var listMapper: ApiMapper<List<Post>, List<PostDto>>

    @Mock
    lateinit var detailMapper: ApiMapper<Post, PostDto>

    @Mock
    lateinit var context: Context

    private lateinit var repository: IPostsRepository
    private lateinit var useCases: PostsUseCases
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        repository = PostRepoImpl(
            remoteDataSource = postRemoteDatasource,
            authRemoteDataSource = authRemoteDataSource,
            getPostApiMapper = listMapper,
            getPostDetailApiMapper = detailMapper
        )

        useCases = PostsInteractor(repository)
        viewModel = HomeViewModel(useCases)
    }

    @Test
    fun `create post success should update state`() = runTest {
        whenever(
            authRemoteDataSource.getCurrentUserId()
        ).thenReturn("user123")

        whenever(
            postRemoteDatasource.createPost(any())
        ).thenReturn(Unit)

        val request = RequestPost(
            mediaUri = null,
            oldMediaUrl = null,
            oldMediaType = null,
            description = "hello"
        )

        viewModel.createPost(
            request,
            context
        )
        advanceUntilIdle()
        assertFalse(viewModel.postState.value.isLoading)
        assertNull(viewModel.postState.value.error)

        verify(postRemoteDatasource).createPost(any())
    }

    @Test
    fun `create post error should return error message`() = runTest {
        whenever(
            authRemoteDataSource.getCurrentUserId()
        ).thenReturn("user123")
        whenever(
            postRemoteDatasource.createPost(any())
        ).thenThrow(RuntimeException("Database Error"))

        val request = RequestPost(
            mediaUri = null,
            oldMediaUrl = null,
            oldMediaType = null,
            description = null
        )

        viewModel.createPost(
            request, context
        )
        advanceUntilIdle()
        assertEquals(
            "Database Error",
            viewModel.postState.value.error
        )
        assertNotNull(viewModel.postState.value.error)
        assertFalse(viewModel.postState.value.isLoading)

        verify(postRemoteDatasource).createPost(any())
    }

    @Test
    fun `test for uploading media`() = runTest {
        whenever(
            authRemoteDataSource.getCurrentUserId()
        ).thenReturn("user123")

        val uri = mock(Uri::class.java)
        whenever(
            postRemoteDatasource.uploadMedia(
                uri = uri,
                context = context
            )
        ).thenReturn(
            UploadMediaResult(
                mediaUrl = "url",
                mediaType = "IMAGE"
            )
        )

        whenever(
            postRemoteDatasource.createPost(
                any()
            )
        ).thenReturn(Unit)

        val request = RequestPost(
            mediaUri = uri,
            oldMediaUrl = null,
            oldMediaType = null,
            description = "hello"
        )

        viewModel.createPost(
            request, context
        )

        advanceUntilIdle()

        verify(postRemoteDatasource).createPost(any())
        verify(postRemoteDatasource).uploadMedia(uri, context)
    }

}