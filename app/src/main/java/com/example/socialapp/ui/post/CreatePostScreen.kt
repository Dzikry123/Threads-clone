package com.example.socialapp.ui.post

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.core.domain.model.post.RequestPost
import com.example.core.utils.MediaType
import com.example.socialapp.ui.components.AudioPlayer
import com.example.socialapp.ui.components.VideoPlayer
import com.example.socialapp.ui.navigation.NavigateEvent
import com.example.socialapp.ui.viewmodel.HomeViewModel
import com.example.socialapp.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    modifier: Modifier = Modifier,
    onBackHome: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()

) {
    val context = LocalContext.current
    var descriptionValue by remember { mutableStateOf("") }
    var selectedMediaType by remember { mutableStateOf<MediaType?>(null) }
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }

    val profileState by profileViewModel
        .profileState
        .collectAsStateWithLifecycle()

    // Launcher Media
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedMediaUri = uri
        selectedMediaType = if (uri != null) MediaType.IMAGE else null
    }

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedMediaUri = uri
        selectedMediaType = if (uri != null) MediaType.VIDEO else null
    }

    val audioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        selectedMediaUri = uri
        selectedMediaType = if (uri != null) MediaType.AUDIO else null
    }

    val state by homeViewModel.postState.collectAsState()

    // Handling Navigation Event
    LaunchedEffect(Unit) {
        homeViewModel.event.collect { event ->
            when (event) {
                NavigateEvent.PopUp -> onBackHome()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "New thread",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBackHome) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // Bagian bawah layar: Menampilkan tombol "Post" dan status loading
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Anyone can reply",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                    )

                    Button(
                        onClick = {
                            homeViewModel.createPost(
                                RequestPost(
                                    mediaUri = selectedMediaUri,
                                    description = descriptionValue
                                ),
                                context
                            )
                        },
                        enabled = !state.isLoading && (descriptionValue.isNotBlank() || selectedMediaUri != null),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.background
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.background,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Post", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sisi Kiri: Foto Profil & Thread Line
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(42.dp)
                ) {
                    // Placeholder Avatar Pengguna
                    AsyncImage(
                        model = profileState.profile?.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    // Garis penghubung vertikal khas Threads (jika ada input/media di bawahnya)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .weight(1f, fill = false)
                            .heightIn(min = 60.dp)
                            .background(Color.LightGray.copy(alpha = 0.5f))
                    )
                }

                // Sisi Kanan: Input Form & Media Preview
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    profileState.profile?.username?.let {
                        Text(
                            text = it,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    // Input Description (Bisa digunakan untuk detail tambahan/sub-thread)
                    BasicTextField(
                        value = descriptionValue,
                        onValueChange = { descriptionValue = it },
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            if (descriptionValue.isEmpty()) {
                                Text(
                                    text = "Add details/description...",
                                    color = Color.Gray.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    // Preview Media dengan Tombol Hapus (X) di pojok kanan atas media
                    selectedMediaUri?.let { uri ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            when (selectedMediaType) {
                                MediaType.IMAGE -> {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 280.dp)
                                    )
                                }
                                MediaType.VIDEO -> {
                                    VideoPlayer(mediaUri = uri)
                                }
                                MediaType.AUDIO -> {
                                    AudioPlayer(mediaUri = uri)
                                }
                                null -> Unit
                            }

                            // Tombol Hapus Media terpilih
                            IconButton(
                                onClick = {
                                    selectedMediaUri = null
                                    selectedMediaType = null
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(28.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Media",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Deretan Icon Pemilih Media (Gallery, Video, Audio) ala toolbar Threads
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                imageLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = "Pick Image",
                                tint = Color.Gray
                            )
                        }

                        IconButton(
                            onClick = {
                                videoLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Videocam,
                                contentDescription = "Pick Video",
                                tint = Color.Gray
                            )
                        }

                        IconButton(
                            onClick = {
                                val mimeTypes = arrayOf("audio/mpeg", "audio/wav", "audio/mp4", "audio/x-wav")
                                audioLauncher.launch(mimeTypes)
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AudioFile,
                                contentDescription = "Pick Audio",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }

            // Log Error Indicator (Akan muncul di bagian bawah jika terjadi error)
            if (state.error != null) {
                Log.d("Error create post", "${state.error}")
                Text(
                    text = "Gagal mengunggah thread. Silakan coba lagi.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}