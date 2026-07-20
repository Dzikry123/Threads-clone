package com.example.core.domain.model.post

import android.net.Uri
import com.example.core.utils.MediaType

data class RequestPost(
    val mediaUri: Uri? = null,
    val oldMediaUrl: String? = null,
    val oldMediaType: MediaType? = null,
    val description: String
)

data class UploadMediaResult(
    val mediaUrl: String,
    val mediaType: String
)
