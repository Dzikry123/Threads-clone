package com.example.core.domain.model.profile

data class Profile(
    val id: String,
    val username: String,
    val fullName: String,
    val bio: String,
    val avatarUrl: String?,
    val createdAt: String?,
    val updatedAt: String?,
)
