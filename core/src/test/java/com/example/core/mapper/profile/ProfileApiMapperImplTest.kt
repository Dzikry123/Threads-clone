package com.example.core.mapper.profile

import com.example.core.data.remote.responses.ProfileDto
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Test


class ProfileApiMapperImplTest {
    private lateinit var mapper: ProfileApiMapperImpl

    @Before
    fun setup() {
        mapper = ProfileApiMapperImpl()
    }

    @Test
    fun `map domain should map all fields correctly`() {
        val dto = ProfileDto(
            id = "1",
            username = "asep",
            fullName = "Asep Setiawan",
            bio = "dev",
            avatarUrl = "https://example.com/avatar.jpg",
            createdAt = "2025-01-02",
            updatedAt = "2025-01-03"
        )
        val result = mapper.mapToDomain(dto)

        assertEquals("1", result.id)
        assertEquals("asep", result.username)
        assertEquals("Asep Setiawan", result.fullName)
        assertEquals("dev", result.bio)
        assertEquals("https://example.com/avatar.jpg", result.avatarUrl)
        assertEquals("2025-01-02", result.createdAt)
        assertEquals("2025-01-03", result.updatedAt)

    }

    @Test
    fun `mapToDomain should return empty when username fullname and bio are null`() {
        val dto = ProfileDto(
            id = "1",
            username = null,
            fullName = null,
            bio = null,
            avatarUrl = null,
            createdAt = null,
            updatedAt = null
        )

        val result = mapper.mapToDomain(dto)

        assertEquals("1", result.id)
        assertEquals("Empty", result.username)
        assertEquals("Empty", result.fullName)
        assertEquals("Empty", result.bio)
        assertNull(result.avatarUrl)
        assertNull(result.createdAt)
        assertNull(result.updatedAt)

    }
}