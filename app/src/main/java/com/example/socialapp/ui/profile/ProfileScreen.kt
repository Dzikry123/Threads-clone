package com.example.socialapp.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Segment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.example.socialapp.ui.components.EmptyFeedNoHeader
import com.example.socialapp.ui.components.PostCard
import com.example.socialapp.ui.viewmodel.AuthViewModel
import com.example.socialapp.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onUpdateProfileClick: () -> Unit,
    onPostClick: (id: String) -> Unit,
    onLogoutClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
) {
//    LaunchedEffect(Unit) {
//        profileViewModel.getProfileUser()
//    }

    val profileState by profileViewModel.profileState.collectAsStateWithLifecycle()
    val postsState = profileViewModel.postsById.collectAsLazyPagingItems()

//    if (profileState.isLoading) {
//        CircularProgressIndicator()
//    }
//
//    if (profileState.error != null) {
//        Log.e("PROFILE_STATE", profileState.error!!)
//        Text("Error")
//    }

    when {
        profileState.profile == null && profileState.isLoading -> {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground, strokeWidth = 2.dp)
            }
        }

        profileState.profile == null && profileState.error != null -> {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(profileState.error!!)
            }
        }

        profileState.profile != null -> {
            profileState.profile?.let { profile ->
                val isRefreshing =
                    postsState.loadState.refresh is LoadState.Loading &&
                            postsState.itemCount > 0

                Scaffold(
                    topBar = {
                        CustomTopAppBarWithDivider()
                    }
                ) { padding ->
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            postsState.refresh()
                            profileViewModel.refresh()
                        },
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                    )
                    {
                        Box(
                            modifier
                                .fillMaxSize()
                        ) {
                            LazyColumn(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                item {

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 16.dp)
                                    ) {

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.Top
                                        ) {

                                            Column(
                                                modifier = Modifier.weight(1f)
                                            ) {

                                                Text(
                                                    text = profile.fullName ?: "",
                                                    fontSize = 28.sp,
                                                    fontWeight = FontWeight.Bold
                                                )

                                                Spacer(Modifier.height(6.dp))

                                                Text(
                                                    text = "@${profile.username}",
                                                    fontSize = 15.sp,
                                                    color = Color.Gray
                                                )

                                                Spacer(Modifier.height(12.dp))

                                                if (!profile.bio.isNullOrBlank()) {
                                                    Text(
                                                        text = profile.bio,
                                                        fontSize = 16.sp,
                                                        lineHeight = 22.sp
                                                    )
                                                }

                                                Spacer(Modifier.height(16.dp))

                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {

                                                    Text(
                                                        "0 followers",
                                                        color = Color.Gray,
                                                        fontSize = 14.sp
                                                    )

                                                    Spacer(Modifier.width(16.dp))

                                                    Text(
                                                        "0 following",
                                                        color = Color.Gray,
                                                        fontSize = 14.sp
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.width(16.dp))

                                            AsyncImage(
                                                model = profile.avatarUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(84.dp)
                                                    .clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                        Spacer(Modifier.height(8.dp))
                                        OutlinedButton(
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = onUpdateProfileClick,
                                            shape = RoundedCornerShape(10.dp), // Threads menggunakan corner radius sedikit lebih tegas
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), // Garis pembatas tipis ala Threads
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.onBackground // Warna teks mengikuti tema
                                            )
                                        ) {
                                            Text(
                                                text = "Edit profile",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = 0.2.sp
                                            )
                                        }

                                        Spacer(Modifier.height(12.dp)) // Jarak antar tombol di Threads sedikit lebih rapat

                                        OutlinedButton(
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = {
                                                authViewModel.logout()
                                                onLogoutClick()
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.onBackground
                                            )
                                        ) {
                                            Text(
                                                text = "Log Out",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = 0.2.sp
                                            )
                                        }

                                        Spacer(Modifier.height(18.dp))

                                        HorizontalDivider()

                                        Spacer(Modifier.height(14.dp))


                                    }
                                }
                                when (val refresh = postsState.loadState.refresh) {
                                    is LoadState.Loading -> {
                                        item {
                                            Box(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(32.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground, strokeWidth = 2.dp)
                                            }
                                        }
                                    }

                                    is LoadState.Error -> {
                                        item {
                                            Text(
                                                refresh.error.message
                                                    ?: "Unknown Error"
                                            )
                                        }
                                    }

                                    is LoadState.NotLoading -> {
                                        if (postsState.itemCount == 0) {
                                            item {
                                                EmptyFeedNoHeader()
                                            }
                                        } else {
                                            items(
                                                count = postsState.itemCount,
                                                key = {
                                                    postsState[it]?.id ?: it
                                                }
                                            ) { index ->
                                                postsState[index]?.let { post ->
                                                    PostCard(
                                                        post = post,
                                                        onPostClick = onPostClick
                                                    )
                                                    HorizontalDivider(
                                                        thickness = .6.dp,
                                                        color = Color.DarkGray
                                                    )

                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBarWithDivider() {
    Column {
        TopAppBar(
            title = {
                Text(
                    "SocialApp",
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {

                IconButton(
                    onClick = {
                        // Search
                    }
                ) {
                    Icon(
                        Icons.Default.Search,
                        null
                    )
                }

                IconButton(
                    onClick = {
                        // Settings
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Segment,
                        null
                    )
                }
            }
        )
        HorizontalDivider(
            thickness = 1.dp,              // Ketebalan garis pembatas
            color = Color.Gray      // Warna abu-abu halus (Light Gray)
        )
    }
}


