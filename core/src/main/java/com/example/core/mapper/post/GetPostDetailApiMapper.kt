package com.example.core.mapper.post

import com.example.core.data.remote.responses.PostDto
import com.example.core.domain.model.post.Post
import com.example.core.mapper.ApiMapper
import com.example.core.utils.MediaType
import com.example.core.utils.toTimeAgo

class GetPostDetailApiMapperImpl : ApiMapper<Post, PostDto> {
    override fun mapToDomain(apiDto: PostDto): Post {
        return Post(
            id = apiDto.id,
            userId = apiDto.userId,
            username = apiDto.username,
            avatarUrl = apiDto.avatarUrl,
            mediaUrl = apiDto.mediaUrl,
            mediaType =  when(apiDto.mediaType?.uppercase()) {
                "IMAGE" -> MediaType.IMAGE

                "VIDEO" -> MediaType.VIDEO

                "AUDIO" -> MediaType.AUDIO

                else -> null
            },
            description = apiDto.description,
            createdAt = apiDto.createdAt?.toTimeAgo(),
            updatedAt = apiDto.updatedAt
        )
    }
}