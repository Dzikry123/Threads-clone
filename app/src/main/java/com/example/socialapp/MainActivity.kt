package com.example.socialapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.socialapp.ui.navigation.BottomNavItem
import com.example.socialapp.ui.navigation.Route
import com.example.socialapp.ui.navigation.SocialAppNavGraph
import com.example.socialapp.ui.theme.SocialAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SocialAppTheme {
                App()
            }
        }
    }

    @Composable
    fun App() {
        // 1. Sisipkan CreatePost di bagian tengah daftar item
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.CreatePost, // Berada di tengah-tengah
            BottomNavItem.Profile,
        )

        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val showBottomBar =
            currentRoute?.startsWith(Route.Splash.route) != true &&
                    currentRoute?.startsWith(Route.Login.route) != true &&
                    currentRoute?.startsWith(Route.Register.route) != true &&
                    currentRoute?.startsWith(Route.DetailPost.route) != true &&
                    currentRoute?.startsWith(Route.CreatePost.route) != true &&
                    currentRoute?.startsWith(Route.UpdatePost.route) != true &&
                    currentRoute?.startsWith(Route.UpdateProfile.route) != true

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    val borderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)

                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.background,
                        modifier = Modifier.drawBehind {
                            drawLine(
                                color = borderColor,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 0.5.dp.toPx()
                            )
                        },
                        tonalElevation = 0.dp
                    ) {
                        items.forEach { item ->
                            // 2. Tombol CreatePost tidak pernah dianggap "selected" di bottom bar
                            // agar highlight tab (Home/Profile) tidak hilang saat user menulis post
                            val isSelected = currentRoute == item.route

                            NavigationBarItem(
                                selected = isSelected,
                                label = null,
                                alwaysShowLabel = false,
                                onClick = {
                                    if (item == BottomNavItem.CreatePost) {
                                        // 3. Penanganan Khusus Create Post: Lompat langsung ke screen tanpa menumpuk backstack utama
                                        navController.navigate(Route.CreatePost.route)
                                    } else if (!isSelected) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        // Karena CreatePost hanya bersifat shortcut, gunakan ikon outlined/kustom Anda
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(26.dp)
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onBackground,
                                    unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        ) { padding ->
            SocialAppNavGraph(
                navHostController = navController,
                modifier = Modifier.padding(padding)
            )
        }
    }
}