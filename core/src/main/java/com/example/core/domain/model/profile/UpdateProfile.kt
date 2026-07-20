package com.example.core.domain.model.profile

import android.net.Uri

data class UpdateProfile(
    val id: String,
    val username: String,
    val fullName: String,
    val bio: String,
    val avatarUrl: String?,
    val mediaUri: Uri? = null,
)
