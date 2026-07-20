package com.example.core.data.remote.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(

    @SerialName("id")
    val id: String,

    @SerialName("username")
    val username: String? = null,

    @SerialName("full_name")
    val fullName: String? = null,

    @SerialName("bio")
    val bio: String? = null,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)
