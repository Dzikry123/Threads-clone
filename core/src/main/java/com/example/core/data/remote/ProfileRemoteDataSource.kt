package com.example.core.data.remote

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.core.data.remote.responses.ProfileDto
import com.example.core.domain.model.post.UploadMediaResult
import com.example.core.domain.model.profile.UpdateProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import java.util.UUID
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {

    suspend fun getProfileUser(): ProfileDto {
        val userId =
            supabaseClient.auth.currentUserOrNull()?.id
                ?: throw IllegalArgumentException(
                    "User not logged in"
                )
        Log.d("PROFILE", "userId = $userId")
        val result = supabaseClient
            .from("profiles")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingle<ProfileDto>()

        Log.d("PROFILE", result.toString())
        return result
    }

    suspend fun updateProfileUser(
        request: UpdateProfile
    ) {
        val userId =
            supabaseClient.auth.currentUserOrNull()?.id
                ?: throw IllegalArgumentException(
                    "User not logged in"
                )
         supabaseClient
            .from("profiles")
            .update(
                {
                    set("username", request.username)
                    set("full_name", request.fullName)
                    set("bio", request.bio)
                    set("avatar_url", request.avatarUrl)
                }
            ) {
                filter {
                    eq("id", userId)
                }
            }
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
}