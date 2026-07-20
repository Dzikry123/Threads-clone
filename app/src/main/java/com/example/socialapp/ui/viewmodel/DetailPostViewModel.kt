package com.example.socialapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.post.Post
import com.example.core.domain.model.post.RequestPost
import com.example.core.domain.usecases.auth.AuthUseCases
import com.example.core.domain.usecases.posts.PostsUseCases
import com.example.core.utils.handleResponse
import com.example.socialapp.ui.navigation.NavigateEvent
import com.example.socialapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailPostViewModel @Inject constructor(
    private val useCases: PostsUseCases,
    private val authUseCases: AuthUseCases,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _postState = MutableStateFlow(DetailPostState())
    val postState = _postState.asStateFlow()

    val idUser = authUseCases.checkIdUser() ?: "ID Undefined"
    // buat satu val untuk get id post pakai parcelable check tmdb app terus masukin getPostBy ke dalam init
    val postId: String = savedStateHandle.get<String>(Route.DetailPost.ARG_ID)
        ?: ""

    private val _event = MutableSharedFlow<NavigateEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val event = _event.asSharedFlow()

    init {
        getPostById(postId)
    }

    private fun getPostById(postId: String) {
        viewModelScope.launch {
            useCases.getPostById(postId).handleResponse(
                onError = { e ->
                    _postState.update {
                        it.copy(
                            isLoading = false,
                            error = e?.message
                        )
                    }
                },
                onLoading = {
                    _postState.update {
                        it.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                },
            ) { detailPost ->
                _postState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        isSuccess = true,
                        detailPost = detailPost
                    )
                }
            }
        }
    }


    fun updatePost(postId: String, context: Context, data: RequestPost) {
        viewModelScope.launch {
            useCases.updatePost(postId, context, data).handleResponse(
                onError = { e ->
                    _postState.update {
                        it.copy(
                            isLoading = false,
                            error = e?.message
                        )
                    }
                },
                onLoading = {
                    _postState.update {
                        it.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                },
            ) {
                _postState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                    )
                }
            }
            _event.emit(NavigateEvent.PopUp)
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            useCases.deletePost(postId).handleResponse(
                onError = { e ->
                    _postState.update {
                        it.copy(
                            isLoading = false,
                            error = e?.message
                        )
                    }
                },
                onLoading = {
                    _postState.update {
                        it.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                },
            ) {
                _postState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                    )
                }
            }
            _event.emit(NavigateEvent.PopUp)
        }
    }

}

data class DetailPostState(
    val detailPost: Post? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
)