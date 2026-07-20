package com.example.core.domain.model.post

import com.example.core.utils.MediaType

data class Post(
    val id: String,
    val userId: String,
    val username: String,
    val avatarUrl: String,
    val mediaUrl: String?,
    val mediaType: MediaType?,
    val description: String,
    val createdAt: String?,
    val updatedAt: String?,
)
