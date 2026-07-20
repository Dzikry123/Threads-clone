package com.example.core.utils

import android.util.Log
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.time.OffsetDateTime

suspend fun <T> Flow<Response<T>>.handleResponse(
    onError: (Throwable?) -> Unit = {
        Log.e("handleResponse", "Handle Response $it")
    },
    onLoading: () -> Unit = {},
    onSuccess: (T) -> Unit
) {
    collect { response ->
        when(response) {
            is Response.Error -> {
                onError(response.error)
            }
            is Response.Success -> {
                onSuccess(response.data)
            }
            is Response.Loading -> {
                onLoading()
            }
        }
    }
}

fun String.toTimeAgo(): String {
    return try {
        val createdTime = OffsetDateTime.parse(this)
        val now = OffsetDateTime.now()
        val duration = Duration.between(createdTime, now)
        val seconds = duration.seconds

        when {
            seconds < 60 ->
                "${seconds}s ago"

            seconds < 3600 ->
                "${seconds / 60}m ago"

            seconds < 86400 ->
                "${seconds / 3600}h ago"

            seconds < 604800 ->
                "${seconds / 86400}d ago"

            seconds < 2629746 ->
                "${seconds / 604800}w ago"

            seconds < 31556952 ->
                "${seconds / 2629746}mo ago"

            else ->
                "${seconds / 31556952}y ago"
        }
    } catch (e: Exception) {
        this
    }
}

fun formatTime(ms: Long): String {

    val totalSeconds = ms / 1000

    val minutes = totalSeconds / 60

    val seconds = totalSeconds % 60

    return String.format("%d:%02d", minutes, seconds)

}