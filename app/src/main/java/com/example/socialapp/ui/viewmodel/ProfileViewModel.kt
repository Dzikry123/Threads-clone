package com.example.socialapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.core.domain.model.profile.Profile
import com.example.core.domain.model.profile.UpdateProfile
import com.example.core.domain.usecases.auth.AuthUseCases
import com.example.core.domain.usecases.posts.PostsUseCases
import com.example.core.domain.usecases.profile.ProfileUseCases
import com.example.core.utils.handleResponse
import com.example.socialapp.ui.navigation.NavigateEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileUseCases: ProfileUseCases,
    private val postUseCases: PostsUseCases,
    private val authUseCases: AuthUseCases

): ViewModel() {
    private val _profileState = MutableStateFlow(ProfileState())
    val profileState = _profileState.asStateFlow()
    private val _postState = MutableStateFlow(_root_ide_package_.com.example.socialapp.ui.viewmodel.PostState())
    val postState = _postState.asStateFlow()

    val idUser = authUseCases.checkIdUser() ?: "ID Undefined"

    private val _event = MutableSharedFlow<NavigateEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val event = _event.asSharedFlow()

    private var profileJob: Job? = null

    init {
        getProfileUser()
//        getPostsByUserId()
    }

    fun refresh() {
        getProfileUser()
    }


    fun getProfileUser() {

        profileJob?.cancel()

        profileJob = viewModelScope.launch {

            profileUseCases.getProfileUser()
                .handleResponse(
                    onLoading = {
                        _profileState.update {
                            it.copy(
                                isLoading = true,
                                error = null
                            )
                        }
                    },
                    onError = { e ->
                        _profileState.update {
                            it.copy(
                                isLoading = false,
                                error = e?.message
                            )
                        }
                    }
                ) { profile ->
                    _profileState.update {
                        it.copy(
                            isLoading = false,
                            profile = profile,
                            error = null
                        )
                    }
                }
        }
    }

    fun updateProfileUser(profile: UpdateProfile, context: Context) {
        viewModelScope.launch {
            profileUseCases.updateProfileUser(profile, context).handleResponse(
                onError = { e ->
                    _profileState.update {
                        it.copy(
                            isLoading = false,
                            error = e?.message
                        )
                    }
                },
                onLoading = {
                    _profileState.update {
                        it.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                },
            ) { profile ->
                _profileState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                    )
                }
            }
            _event.emit(NavigateEvent.PopUp)
        }
    }

    val postsById = postUseCases
        .getPostsByUserId(idUser)
        .cachedIn(viewModelScope)

//    fun getPostsByUserId() {
//        viewModelScope.launch {
//            postUseCases.getPostsByUserId(idUser).handleResponse(
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
}

data class ProfileState(
    val profile: Profile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)