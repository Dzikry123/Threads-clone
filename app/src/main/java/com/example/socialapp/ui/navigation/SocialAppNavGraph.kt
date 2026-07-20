package com.example.socialapp.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.socialapp.ui.HomeScreen
import com.example.socialapp.ui.auth.LoginScreen
import com.example.socialapp.ui.post.CreatePostScreen
import com.example.socialapp.ui.post.DetailPostScreen
import com.example.socialapp.ui.post.UpdatePostScreen
import com.example.socialapp.ui.profile.ProfileScreen
import com.example.socialapp.ui.profile.UpdateProfileScreen
import com.example.socialapp.ui.splashscreen.SplashScreen
import com.example.socialapp.ui.viewmodel.AuthState
import com.example.socialapp.ui.viewmodel.AuthViewModel

@Composable
fun SocialAppNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        Log.d(
            "AUTH STATE NAV",
            "authState=$authState"
        )
        when (authState) {
            AuthState.Loading -> {
                Unit
            }

            AuthState.Unauthenticated -> {
                navHostController.navigate(Route.Login.route) {
                    // Bersihkan backstack hingga ke halaman Home
                    popUpTo(Route.Home.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            AuthState.Authenticated -> {
                navHostController.navigate(Route.Home.route) {
                    popUpTo(Route.Login.route) {
                        inclusive = true
                    }
                    // Jika dari splash screen, ikut bersihkan splash-nya
                    popUpTo(Route.Splash.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = Route.Splash.route
    ) {
        composable(Route.Splash.route) {
            SplashScreen()
        }
        composable(Route.Login.route) {
            LoginScreen(viewModel)
        }
        composable(Route.Home.route) {
            HomeScreen(
                onCreateClick = {
                    navHostController.navigate(Route.CreatePost.route)
                },
                onPostClick = { id ->
                    navHostController.navigate(Route.DetailPost.createRoute(id))
                },
            )
        }

        composable(Route.CreatePost.route) {
            CreatePostScreen(
                onBackHome = {
                    navHostController.popBackStack()
                }
            )
        }

        composable(
            route = Route.DetailPost.routeWithArgs,
            arguments = listOf(
                navArgument(Route.DetailPost.ARG_ID) {
                    type = NavType.StringType
                }
            )
        ) {
            DetailPostScreen(
                onUpdateClick = { id ->
                    navHostController.navigate(Route.UpdatePost.createRoute(id))
                },
                onBackHome = {
                    navHostController.popBackStack()
                }
            )
        }

        composable(
            route = Route.UpdatePost.routeWithArgs,
            arguments = listOf(
                navArgument(Route.UpdatePost.ARG_ID) {
                    type = NavType.StringType
                }
            )
        ) {
            UpdatePostScreen(
                onBackHome = {
                    navHostController.popBackStack()
                }
            )
        }

        // Profile
        composable(Route.Profile.route) {
            ProfileScreen(
                onPostClick = { id ->
                    navHostController.navigate(Route.DetailPost.createRoute(id))
                },
                onUpdateProfileClick = {
                    navHostController.navigate(Route.UpdateProfile.route)
                },
                onLogoutClick = {
                    // Navigasi manual ke Login
                    navHostController.navigate(Route.Login.route) {
                        // Pop semua screen sampai ke root (0 = root)
                        popUpTo(0) {
                            inclusive = true // Hapus semua termasuk Login jika ada
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Route.UpdateProfile.route,
        ) {
            UpdateProfileScreen(
                onBackHome = {
                    navHostController.popBackStack()
                }
            )
        }
    }

}