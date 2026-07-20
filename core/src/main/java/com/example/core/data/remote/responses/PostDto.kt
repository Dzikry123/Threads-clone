package com.example.core.data.remote.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("username")
    val username: String,

    @SerialName("avatar_url")
    val avatarUrl: String,

    @SerialName("media_url")
    val mediaUrl: String?,

    @SerialName("media_type")
    val mediaType: String?,

    @SerialName("description")
    val description: String,

    @SerialName("created_at")
    val createdAt: String?,

    @SerialName("updated_at")
    val updatedAt: String?,
)