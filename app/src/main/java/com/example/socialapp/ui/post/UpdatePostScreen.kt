package com.example.socialapp.ui.post

import android.net.Uri
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
import com.example.socialapp.ui.viewmodel.DetailPostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePostScreen(
    modifier: Modifier = Modifier,
    onBackHome: () -> Unit,
    detailPostViewModel: DetailPostViewModel = hiltViewModel()
) {
    val state by detailPostViewModel.postState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        detailPostViewModel.event.collect { event ->
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
                        "Edit thread",
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
        }
    ) { innerPadding ->

        // State Loading & Error Global
        if (state.isLoading && state.detailPost == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        if (state.error != null && state.detailPost == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error loading post data", color = MaterialTheme.colorScheme.error)
            }
        }

        // Tampilkan Form jika data post berhasil dimuat
        if (state.detailPost != null) {
            val detailPost = state.detailPost!!
            val context = LocalContext.current

            var descriptionValue by remember { mutableStateOf(detailPost.description) }

            // State Media Baru Terpilih
            var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
            var selectedMediaType by remember { mutableStateOf<MediaType?>(null) }

            // State untuk melacak apakah media lama dihapus
            var isMediaDeleted by remember { mutableStateOf(false) }

            // Launcher Media
            val imageLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                if (uri != null) {
                    selectedMediaUri = uri
                    selectedMediaType = MediaType.IMAGE
                    isMediaDeleted = false
                }
            }

            val videoLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                if (uri != null) {
                    selectedMediaUri = uri
                    selectedMediaType = MediaType.VIDEO
                    isMediaDeleted = false
                }
            }

            val audioLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
            ) { uri ->
                if (uri != null) {
                    selectedMediaUri = uri
                    selectedMediaType = MediaType.AUDIO // Fix: Sebelumnya tertulis VIDEO
                    isMediaDeleted = false
                }
            }

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Sisi Kiri: Avatar & Thread Line
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(42.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            ) {
                                Text("U", modifier = Modifier.align(Alignment.Center), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .weight(1f, fill = false)
                                    .heightIn(min = 60.dp)
                                    .background(Color.LightGray.copy(alpha = 0.5f))
                            )
                        }

                        // Sisi Kanan: Form input teks, Tampilan Media, & Tombol Aksi
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "username_anda", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                            // Input Description
                            BasicTextField(
                                value = descriptionValue,
                                onValueChange = { descriptionValue = it },
                                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (descriptionValue.isEmpty()) Text("Add details/description...", color = Color.Gray.copy(alpha = 0.7f), fontSize = 14.sp)
                                    innerTextField()
                                }
                            )

                            // Render Media Section (Prioritas Media Baru, lalu Media Lama)
                            val hasMediaToShow = (selectedMediaUri != null) || (!detailPost.mediaUrl.isNullOrEmpty() && !isMediaDeleted)
                            if (hasMediaToShow) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 280.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    if (selectedMediaUri != null) {
                                        // Menampilkan Media Baru terpilih dari galeri lokal
                                        when (selectedMediaType) {
                                            MediaType.IMAGE -> AsyncImage(model = selectedMediaUri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp))
                                            MediaType.VIDEO -> VideoPlayer(mediaUri = selectedMediaUri)
                                            MediaType.AUDIO -> AudioPlayer(mediaUri = selectedMediaUri)
                                            null -> Unit
                                        }
                                    } else {
                                        // Menampilkan Media Lama dari Server URL
                                        when (detailPost.mediaType) {
                                            MediaType.IMAGE -> AsyncImage(model = detailPost.mediaUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp))
                                            MediaType.VIDEO -> VideoPlayer(mediaUrl = detailPost.mediaUrl)
                                            MediaType.AUDIO -> AudioPlayer(mediaUrl = detailPost.mediaUrl)
                                            null -> Unit
                                        }
                                    }

                                    // Tombol Hapus Media (Silang Melayang)
                                    IconButton(
                                        onClick = {
                                            selectedMediaUri = null
                                            selectedMediaType = null
                                            isMediaDeleted = true
                                        },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .size(28.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Remove Media", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Toolbar Pemilih Media Mini ala Threads
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { imageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, modifier = Modifier.size(24.dp)) {
                                    Icon(imageVector = Icons.Outlined.Image, contentDescription = "Change Image", tint = Color.Gray)
                                }
                                IconButton(onClick = { videoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) }, modifier = Modifier.size(24.dp)) {
                                    Icon(imageVector = Icons.Outlined.Videocam, contentDescription = "Change Video", tint = Color.Gray)
                                }
                                IconButton(onClick = { val mimeTypes = arrayOf("audio/mpeg", "audio/wav", "audio/mp4", "audio/x-wav"); audioLauncher.launch(mimeTypes) }, modifier = Modifier.size(24.dp)) {
                                    Icon(imageVector = Icons.Outlined.AudioFile, contentDescription = "Change Audio", tint = Color.Gray)
                                }
                            }
                        }
                    }

                    if (state.error != null) {
                        Text(
                            text = "Cannot Update Thread",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Bottom Action Bar: Tetap berada di bawah layar untuk tombol Simpan/Save
                Surface(
                    tonalElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
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
                                // Kirim mediaUri baru (jika ada), atau null jika media dihapus/tidak diganti
                                val mediaToSend = if (isMediaDeleted) null else selectedMediaUri

                                detailPostViewModel.updatePost(
                                    postId = detailPost.id,
                                    context = context,
                                    RequestPost(
                                        mediaUri = mediaToSend,
                                        oldMediaUrl = if (isMediaDeleted) "" else detailPost.mediaUrl,
                                        oldMediaType = if (isMediaDeleted) null else detailPost.mediaType,
                                        description = descriptionValue
                                    )
                                )
                            },
                            enabled = !state.isLoading && (
                                    descriptionValue.isNotBlank() ||
                                            selectedMediaUri != null ||
                                            (!isMediaDeleted && !detailPost.mediaUrl.isNullOrEmpty())
                                    ),                            colors = ButtonDefaults.buttonColors(
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
                                Text("Done", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}