package com.example.core.mapper.profile

import com.example.core.data.remote.responses.ProfileDto
import com.example.core.domain.model.profile.Profile
import com.example.core.mapper.ApiMapper

class ProfileApiMapperImpl: ApiMapper<Profile, ProfileDto> {
    override fun mapToDomain(apiDto: ProfileDto): Profile {
        return Profile(
            id = apiDto.id,
            username = apiDto?.username ?: "Empty",
            fullName = apiDto.fullName ?: "Empty",
            bio = apiDto.bio ?: "Empty",
            avatarUrl = apiDto.avatarUrl,
            createdAt = apiDto.createdAt,
            updatedAt = apiDto.updatedAt
        )
    }
}