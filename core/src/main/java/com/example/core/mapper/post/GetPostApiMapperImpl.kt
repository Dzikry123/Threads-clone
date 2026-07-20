package com.example.core.mapper.post

import com.example.core.data.remote.responses.PostDto
import com.example.core.domain.model.post.Post
import com.example.core.mapper.ApiMapper
import com.example.core.utils.MediaType
import com.example.core.utils.toTimeAgo

class GetPostApiMapperImpl: ApiMapper<List<Post>, List<PostDto>> {
    override fun mapToDomain(apiDto: List<PostDto>): List<Post> {
        return apiDto.map {
            Post(
                id = it.id,
                userId = it.userId,
                username = it.username,
                avatarUrl = it.avatarUrl,
                mediaUrl = it.mediaUrl,
                mediaType = when(it.mediaType?.uppercase()) {
                    "IMAGE" -> MediaType.IMAGE

                    "VIDEO" -> MediaType.VIDEO

                    "AUDIO" -> MediaType.AUDIO

                    else -> null
                },
                description = it.description,
                createdAt = it.createdAt?.toTimeAgo(),
                updatedAt = it.updatedAt
            )
        }
    }
}