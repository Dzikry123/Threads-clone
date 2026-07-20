package com.example.socialapp.ui.components

import android.net.Uri
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.core.utils.formatTime
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayer(
    mediaUri: Uri? = null,
    mediaUrl: String? = null,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    val mediaItem = remember(mediaUri, mediaUrl) {
        when {
            mediaUri != null -> MediaItem.fromUri(mediaUri)
            mediaUrl != null -> MediaItem.fromUri(mediaUrl)
            else -> null
        }
    }

    if (mediaItem == null) return

    val exoPlayer = remember(mediaItem) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(mediaItem)
            prepare()
        }
    }

    var isPlaying by remember {
        mutableStateOf(false)
    }

    var currentPosition by remember {
        mutableLongStateOf(0L)
    }

    var duration by remember {
        mutableLongStateOf(0L)
    }

    LaunchedEffect(Unit) {
        while (true) {

            currentPosition = exoPlayer.currentPosition

            if (exoPlayer.duration > 0)
                duration = exoPlayer.duration

            isPlaying = exoPlayer.isPlaying

            delay(300)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.play()
                    }

                }
            ) {
                Icon(
                    imageVector =
                        if (isPlaying)
                            Icons.Default.PauseCircle
                        else
                            Icons.Default.PlayCircle,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )

            }

            Text(
                text = formatTime(currentPosition),
                modifier = Modifier.width(36.dp),
                style = MaterialTheme.typography.bodySmall
            )

            Slider(
                modifier = Modifier.weight(1f),
                value = currentPosition.toFloat(),
                onValueChange = {
                    exoPlayer.seekTo(it.toLong())
                },
                valueRange = 0f..duration.coerceAtLeast(1L).toFloat(),
                thumb = {
                    Box(
                        modifier = Modifier.scale(0.5f).clip(CircleShape)
                    ) {
                        SliderDefaults.Thumb(
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    }
                },
                track = { sliderState ->
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier = Modifier.height(4.dp) // kecilkan garis
                    )
                }
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = formatTime(duration),
                modifier = Modifier.width(45.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}