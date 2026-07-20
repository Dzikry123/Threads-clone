package com.example.socialapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.example.core.domain.model.post.Post
import com.example.core.domain.model.profile.Profile
import com.example.socialapp.ui.components.EmptyFeed
import com.example.socialapp.ui.components.Header
import com.example.socialapp.ui.components.PostCard
import com.example.socialapp.ui.viewmodel.HomeViewModel
import com.example.socialapp.ui.viewmodel.ProfileState
import com.example.socialapp.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onCreateClick: () -> Unit,
    onPostClick: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val postsState = homeViewModel.posts.collectAsLazyPagingItems()
    val profileState by profileViewModel.profileState.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

    val isRefreshing = postsState.loadState.refresh is LoadState.Loading && postsState.itemCount > 0

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(profile = profileState.profile)
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                ThreadsTopBar(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
        ) { padding ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    postsState.refresh()
                    profileViewModel.refresh()
                },
                state = pullToRefreshState,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                FeedContent(
                    profileState = profileState,
                    postsState = postsState,
                    onCreateClick = onCreateClick,
                    onPostClick = onPostClick
                )
            }
        }
    }
}

@Composable
fun AppDrawer(
    profile: Profile?
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background,
        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
        modifier = Modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp, bottom = 20.dp)
        ) {
            AsyncImage(
                model = profile?.avatarUrl,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = profile?.username ?: "Username",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-0.5).sp
            )
        }

        Spacer(Modifier.height(8.dp))

        val dummyMenus = listOf("Home", "Profile", "Bookmark", "Settings")

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            dummyMenus.forEachIndexed { index, title ->
                val isSelectedPlaceholder = index == 0

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = if (isSelectedPlaceholder) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    selected = isSelectedPlaceholder,
                    onClick = { },
                    shape = RoundedCornerShape(8.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                        unselectedContainerColor = Color.Transparent,
                        selectedTextColor = MaterialTheme.colorScheme.onBackground,
                        unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadsTopBar(
    onMenuClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = {
            Text(
                text = "Threads",
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                letterSpacing = (-1).sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    )
}

@Composable
private fun FeedContent(
    profileState: ProfileState,
    postsState: LazyPagingItems<Post>,
    onCreateClick: () -> Unit,
    onPostClick: (String) -> Unit
) {
    when (val refresh = postsState.loadState.refresh) {
        is LoadState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground, strokeWidth = 2.dp)
            }
        }
        is LoadState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = refresh.error.message ?: "Unknown Error", color = Color.Gray)
            }
        }
        is LoadState.NotLoading -> {
            if (postsState.itemCount == 0) {
                EmptyFeed(onCreateClick)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Header(
                            avatarUser = profileState.profile?.avatarUrl ?: "",
                            onCreateClick = onCreateClick,
                            username = profileState.profile?.username ?: ""
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }

                    items(
                        count = postsState.itemCount,
                        key = { postsState[it]?.id ?: it }
                    ) { index ->
                        postsState[index]?.let { post ->
                            PostCard(post = post, onPostClick = onPostClick)
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                        }
                    }

                    item {
                        if (postsState.loadState.append is LoadState.Loading) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground, strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}


//@Composable
//fun HomeScreen(
//    modifier: Modifier = Modifier,
//    onCreateClick: () -> Unit,
//    onPostClick: (id: String) -> Unit,
//    homeViewModel: HomeViewModel = hiltViewModel(),
//    profileViewModel: ProfileViewModel = hiltViewModel(),
//
//    ) {
//    val state by homeViewModel.postState.collectAsStateWithLifecycle()
//    val profileState by profileViewModel.profileState.collectAsStateWithLifecycle()
//
//    val posts = homeViewModel.posts.collectAsLazyPagingItems()
//
//    // Cara 1
////    LaunchedEffect(Unit) {
////        homeViewModel.getPosts()
////    }
//
//    // Cara 2
////    val lifecycleOwner = LocalLifecycleOwner.current
////
////    DisposableEffect(lifecycleOwner) {
////        val observer = LifecycleEventObserver { _, event ->
////            if (event == Lifecycle.Event.ON_RESUME) {
////                homeViewModel.getPosts()
////            }
////        }
////
////        lifecycleOwner.lifecycle.addObserver(observer)
////
////        onDispose {
////            lifecycleOwner.lifecycle.removeObserver(observer)
////        }
////    }
//
//    if (state.isLoading) {
//        Box(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            CircularProgressIndicator()
//        }
//    }
//
//    if (state.error != null) {
//        Text("Error ${state.error}")
//        Log.d("Error Getting Posts", "msg: ${state.error}")
//    }
//
//    if (!state.isLoading && state.error == null) {
//        Box(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            if (state.posts.isNotEmpty()) {
//                LazyColumn(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .padding(16.dp)
//                ) {
//                    item {
//                        if (profileState.isLoading) {
//                            Box(
//                                modifier = Modifier.fillMaxSize()
//                            ) {
//                                CircularProgressIndicator()
//                            }
//                        }
//
//                        if (profileState.error != null) {
//                            Text("Error ${profileState.error}")
//                            Log.d("Error Getting Profile Data", "msg: ${profileState.error}")
//                        }
//                        if (!profileState.isLoading && profileState.error == null) {
//                            Log.d("PROFILE_PICTURE", "${profileState.profile?.avatarUrl}")
//                            Header(
//                                onCreateClick = onCreateClick,
//                                avatarUser = profileState.profile?.avatarUrl ?: "https://picsum.photos/200/300"
//                            )
//                        } else {
//                            Header(
//                                onCreateClick = onCreateClick,
//                                avatarUser = "https://picsum.photos/200/300"
//                            )
//                        }
//                    }
//                    items(state.posts) { post ->
//                        PostCard(
//                            onPostClick = {
//                                onPostClick(post.id)
//                            },
//                            post = post
//                        )
//                    }
//                }
//            }
//            else {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center,
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxSize()
//                ) {
//                    if (profileState.isLoading) {
//                        Box(
//                            modifier = Modifier.fillMaxSize()
//                        ) {
//                            CircularProgressIndicator()
//                        }
//                    }
//
//                    if (profileState.error != null) {
//                        Text("Error ${profileState.error}")
//                        Log.d("Error Getting Profile Data", "msg: ${profileState.error}")
//                    }
//                    if (!profileState.isLoading && profileState.error == null) {
//                        Log.d("PROFILE_PICTURE", "${profileState.profile?.avatarUrl}")
//                        Header(
//                            onCreateClick = onCreateClick,
//                            avatarUser = profileState.profile?.avatarUrl ?: "https://picsum.photos/200/300"
//                        )
//                    } else {
//                        Header(
//                            onCreateClick = onCreateClick,
//                            avatarUser = "https://picsum.photos/200/300"
//                        )
//                    }
//                    Text(
//                        text = "There is no posts for now, let's create some posts",
//                        style = TextStyle(
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 18.sp
//                        )
//                    )
//                }
//            }
//        }
//    }
//}
//


//

