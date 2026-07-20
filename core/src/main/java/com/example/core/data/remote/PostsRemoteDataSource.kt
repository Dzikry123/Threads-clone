package com.example.core.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.core.data.remote.responses.PostDto
import com.example.core.data.remote.responses.RequestPostDto
import com.example.core.domain.model.post.UploadMediaResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import java.util.UUID
import javax.inject.Inject

class PostsRemoteDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    private fun getCurrentUser(): String {
        val user = supabaseClient.auth.currentUserOrNull()?.id
            ?: throw IllegalArgumentException(
                "User not logged in"
            )
        return user
    }

    suspend fun getPosts(
        from: Long,
        to: Long
    ): List<PostDto> {
        getCurrentUser()
        return supabaseClient
            .from("posts")
            .select {
                order("created_at", Order.DESCENDING)

                range(from, to)
            }
            .decodeList<PostDto>()
    }

    suspend fun getPostsByUserId(
        userId: String,
        from: Long,
        to: Long
    ): List<PostDto> {
        getCurrentUser()
        return supabaseClient
            .from("posts")
            .select {
                filter {
                    eq("user_id", userId)
                }
                range(from, to)
                order("created_at", Order.DESCENDING)
            }
            .decodeList<PostDto>()
    }

    suspend fun getPostById(postId: String): PostDto {
        getCurrentUser()
        Log.d(
            "DETAIL_POST",
            "user=${getCurrentUser()}"
        )
        Log.d("DETAIL_POST", "Before hit Detail Post")
        val result = supabaseClient
            .from("posts")
            .select {
                filter {
                    eq("id", postId)
                }
            }
            .decodeSingle<PostDto>()

        Log.d("DETAIL_POST", "After hit Detail Post")
        Log.d("DETAIL_POST", "result=$result")
        return result
    }

    suspend fun uploadMedia(
        uri: Uri,
        context: Context
    ): UploadMediaResult {
        val mimeType =
            context.contentResolver.getType(uri)

        val bytes =
            context.contentResolver
                .openInputStream(uri)
                ?.readBytes()
                ?: throw Exception(
                    "Cannot read file"
                )

        val (extension, mediaType) =
            when (mimeType) {
                "image/jpeg" -> "jpg" to "IMAGE"
                "image/jpg" -> "jpg" to "IMAGE"
                "image/png" -> "png" to "IMAGE"
                "image/webp" -> "webp" to "IMAGE"

                "video/mp4" -> "mp4" to "VIDEO"
                "video/quicktime" -> "mov" to "VIDEO"

                "audio/mpeg" -> "mp3" to "AUDIO"
                "audio/mp3" -> "mp3" to "AUDIO"
                "audio/wav" -> "wav" to "AUDIO"

                else -> throw IllegalArgumentException(
                    "Unsupported file type : $mimeType"
                )
            }

        val fileName =
            "${UUID.randomUUID()}.$extension"

        supabaseClient.storage
            .from("post-media")
            .upload(
                path = fileName,
                data = bytes
            )

        val publicUrl = supabaseClient.storage
            .from("post-media")
            .publicUrl(fileName)

        return UploadMediaResult(
            mediaUrl = publicUrl,
            mediaType = mediaType
        )
    }

    suspend fun deleteMedia(
        mediaUrl: String
    ) {

        val fileName =
            mediaUrl.substringAfterLast("/")

        supabaseClient.storage
            .from("post-media")
            .delete(fileName)
    }

    suspend fun createPost(data: RequestPostDto) {
        val userId =
            supabaseClient.auth.currentUserOrNull()?.id
                ?: error("User not logged in")
        Log.d(
            "CREATE_POST",
            "user=${getCurrentUser()}"
        )
        Log.d("CREATE_POST", "Before insert")
        val result = supabaseClient
            .from("posts")
            .insert(
                RequestPostDto(
                    userId = userId,
                    mediaUrl = data.mediaUrl,
                    description = data.description,
                    mediaType = data.mediaType
                )
            )

        Log.d("CREATE_POST", "After insert")
        Log.d("CREATE_POST", "result=$result")

    }

    suspend fun updatePost(postId: String, data: RequestPostDto) {
        val userId = getCurrentUser()
        Log.d(
            "UPDATE_POST",
            "user=${getCurrentUser()}"
        )
        Log.d("UPDATE_POST", "Before insert")
        val result = supabaseClient
            .from("posts")
            .update(
                {
                    set("media_url", data.mediaUrl)
                    set("media_type", data.mediaType)
                    set("description", data.description)
                }
            ) {
                filter {
                    eq("id", postId)
                    eq("user_id", userId)
                }
            }
        Log.d("UPDATE_POST", "After insert")
        Log.d("UPDATE_POST", "result=$result")
    }

    suspend fun deletePost(postId: String) {
        val userId = getCurrentUser()
        supabaseClient
            .from("posts")
            .delete {
                filter {
                    eq("id", postId)
                    eq("user_id", userId)
                }
            }
    }
}