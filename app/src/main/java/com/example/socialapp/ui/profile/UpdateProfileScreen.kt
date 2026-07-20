package com.example.socialapp.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.core.domain.model.profile.UpdateProfile
import com.example.socialapp.ui.navigation.NavigateEvent
import com.example.socialapp.ui.viewmodel.ProfileViewModel


@Composable
fun UpdateProfileScreen(
    modifier: Modifier = Modifier,
    onBackHome: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val state by profileViewModel.profileState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        profileViewModel.event.collect { event ->
            when (event) {
                NavigateEvent.PopUp -> {
                    onBackHome()
                }
            }
        }
    }

    if (state.isLoading) {
        CircularProgressIndicator()
    }

    if (state.error != null) {
        Text("Error")
    }

    if (!state.isLoading && state.error == null) {
        state.profile?.let { profile ->
            val context = LocalContext.current
            var usernameValue by remember {
                mutableStateOf(profile.username)
            }
            var fullNameValue by remember {
                mutableStateOf(profile.fullName)
            }
            var bioValue by remember {
                mutableStateOf(profile.bio)
            }

            // 1. Definisikan state untuk menyimpan URI file yang terpilih
            var selectedMediaUri by remember {
                mutableStateOf<Uri?>(null)
            }

            val currentMediaUrl = profile.avatarUrl

            // 2. Perbaikan Launcher: Gambar & Video menggunakan Galeri, Audio menggunakan OpenDocument
            val imageLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(), onResult = { uri: Uri? ->
                    selectedMediaUri = uri // Menyimpan hasil ke state gambar
                })

            Box(
                modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Column {
                    AsyncImage(
                        model = selectedMediaUri ?: profile.avatarUrl,
                        contentDescription = null
                    )

                    // Tombol Gambar (Membuka Galeri khusus Gambar)
                    Button(onClick = {
                        imageLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }) {
                        Text("Pilih Gambar dari Galeri")
                    }

                    // Opsional: Menampilkan indikator jika file sudah terpilih
                    if (selectedMediaUri != null) Text("Media Siap Di Upload!")

                    OutlinedTextField(
                        value = usernameValue,
                        onValueChange = { newValue ->
                            usernameValue = newValue
                        },
                        label = {
                            Text("Username")
                        },
                        placeholder = {
                            Text("hello world")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = fullNameValue,
                        onValueChange = { newValue ->
                            fullNameValue = newValue
                        },
                        label = {
                            Text("Full Name")
                        },
                        placeholder = {
                            Text("lorep ipsum dolor sen amet")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = bioValue,
                        onValueChange = { newValue ->
                            bioValue = newValue
                        },
                        label = {
                            Text("Tell The World About Yourself")
                        },
                        placeholder = {
                            Text("lorep ipsum dolor sen amet")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading,
                        onClick = {
                            profileViewModel.updateProfileUser(
                                profile = UpdateProfile(
                                    mediaUri = selectedMediaUri,
                                    avatarUrl = profile.avatarUrl,
                                    username = usernameValue,
                                    fullName = fullNameValue,
                                    bio = bioValue,
                                    id = profile.id
                                ),
                                context = context
                            )
                        }
                    ) {
                        Text(
                            text = "Update Post"
                        )
                    }

                    if (state.isLoading) {
                        CircularProgressIndicator()
                    }

                    if (state.error != null) {
                        Text("Cannot Update")
                    }
                }
            }
        }
    }
}