package com.example.core.data.remote.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestPostDto(
    @SerialName("user_id")
    val userId: String,

    @SerialName("media_url")
    val mediaUrl: String?,

    @SerialName("media_type")
    val mediaType: String?,

    @SerialName("description")
    val description: String,
)
