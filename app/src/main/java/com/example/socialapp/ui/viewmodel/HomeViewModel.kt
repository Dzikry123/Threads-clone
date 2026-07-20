package com.example.socialapp.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.core.domain.model.post.Post
import com.example.core.domain.model.post.RequestPost
import com.example.core.domain.usecases.posts.PostsUseCases
import com.example.core.utils.handleResponse
import com.example.socialapp.ui.navigation.NavigateEvent
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
class HomeViewModel @Inject constructor(
    private val useCases: PostsUseCases,
) : ViewModel() {
    private val _postState = MutableStateFlow(PostState())
    val postState = _postState.asStateFlow()

    private val _event = MutableSharedFlow<NavigateEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val event = _event.asSharedFlow()

//    init {
//        getPosts()
//    }

    val posts = useCases
        .getPosts()
        .cachedIn(viewModelScope)

//    fun getPosts() {
//        viewModelScope.launch {
//            useCases.getPosts().handleResponse(
//                onError = { e ->
//                    _postState.update {
//                        it.copy(
//                            isLoading = false,
//                            error = e?.message
//                        )
//                    }
//                },
//                onLoading = {
//                    _postState.update {
//                        it.copy(
//                            isLoading = true,
//                            error = null
//                        )
//                    }
//                },
//            ) { posts ->
//                _postState.update {
//                    it.copy(
//                        isLoading = false,
//                        error = null,
//                        posts = posts
//                    )
//                }
//            }
//        }
//    }

    fun createPost(data: RequestPost, context: Context) {
        viewModelScope.launch {
            useCases.createPost(data, context).handleResponse(
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
                Log.d(
                    "CREATE_POST",
                    "SUCCESS"
                )
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

data class PostState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)
