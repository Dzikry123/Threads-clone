package com.example.core.mapper.post

import com.example.core.data.remote.responses.PostDto
import com.example.core.utils.MediaType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test


class GetPostApiMapperImplTest {

    private lateinit var mapper: GetPostApiMapperImpl

    @Before
    fun setup() {
        mapper = GetPostApiMapperImpl()
    }

    @Test
    fun `mapToDomain should map PostDto to Post correctly`() {
        val dto = listOf(
            PostDto(
                id = "1",
                userId = "user123",
                username = "Asep",
                avatarUrl = "avatar.jpg",
                mediaUrl = "image.jpg",
                mediaType = "IMAGE",
                description = "Hello World",
                createdAt = "2026-07-20T10:00:00",
                updatedAt = null
            )
        )

        val result = mapper.mapToDomain(dto)
        assertEquals(1, result.size)

        assertEquals("1", result.first().id)

        assertEquals("user123", result.first().userId)

        assertEquals("Asep", result.first().username)

        assertEquals("avatar.jpg", result.first().avatarUrl)

        assertEquals("image.jpg", result.first().mediaUrl)

        assertEquals(
            MediaType.IMAGE,
            result.first().mediaType
        )

        assertEquals(
            "Hello World",
            result.first().description
        )
    }

    @Test
    fun `should map VIDEO correctly`() {

        val dto = listOf(
            PostDto(
                id = "1",
                userId = "1",
                username = "Asep",
                avatarUrl = "",
                mediaUrl = "",
                mediaType = "VIDEO",
                description = "",
                createdAt = null,
                updatedAt = null
            )
        )

        val result = mapper.mapToDomain(dto)

        assertEquals(
            MediaType.VIDEO,
            result.first().mediaType
        )
    }

    @Test
    fun `should map Audio correctly`() {

        val dto = listOf(
            PostDto(
                id = "1",
                userId = "1",
                username = "Asep",
                avatarUrl = "",
                mediaUrl = "",
                mediaType = "AUDIO",
                description = "",
                createdAt = null,
                updatedAt = null
            )
        )

        val result = mapper.mapToDomain(dto)

        assertEquals(
            MediaType.AUDIO,
            result.first().mediaType
        )
    }

    @Test
    fun `should map MediaType unknown correctly`() {

        val dto = listOf(
            PostDto(
                id = "1",
                userId = "1",
                username = "Asep",
                avatarUrl = "",
                mediaUrl = "",
                mediaType = "PDF",
                description = "",
                createdAt = null,
                updatedAt = null
            )
        )

        val result = mapper.mapToDomain(dto)

        assertNull(result.first().mediaType)
    }

    @Test
    fun `should return empty list`() {
        val dto = emptyList<PostDto>()
        val result = mapper.mapToDomain(dto)
        assertTrue(result.isEmpty())
    }
}