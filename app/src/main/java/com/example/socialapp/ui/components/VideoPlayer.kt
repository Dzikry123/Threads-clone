package com.example.socialapp.ui.components

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    mediaUri: Uri? = null,
    mediaUrl: String? = null,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    // Menyimpan posisi terakhir video
    var playbackPosition by rememberSaveable(mediaUrl) {
        mutableLongStateOf(0L)
    }

    // Menyimpan status play/pause
    var playWhenReady by rememberSaveable(mediaUrl) {
        mutableStateOf(false)
    }

    val mediaItem = remember(mediaUri, mediaUrl) {
        when {
            mediaUri != null ->
                MediaItem.fromUri(mediaUri)

            mediaUrl != null ->
                MediaItem.fromUri(mediaUrl)
            else ->
                null
        }
    }

    if (mediaItem == null) return

    val exoPlayer = remember(mediaItem) {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                setMediaItem(mediaItem)
                prepare()
                seekTo(playbackPosition)
                this.playWhenReady = playWhenReady
            }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playWhenReady = isPlaying
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            playbackPosition = exoPlayer.currentPosition
            playWhenReady = exoPlayer.isPlaying

            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .aspectRatio(16f / 9f),
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = true
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
        },
        update = {
            it.player = exoPlayer
        }
    )
}