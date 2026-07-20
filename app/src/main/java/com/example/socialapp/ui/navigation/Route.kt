package com.example.socialapp.ui.navigation

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Route(val route: String) {

    object Splash: Route("splash")
    object Login: Route("login")
    object Register: Route("register")

    object Home: Route("home")
    object Profile: Route("profile")
    object UpdateProfile: Route("update_profile")

    object CreatePost: Route("create_post")
    object UpdatePost: Route("update_post") {
        const val ARG_ID = "id"

        val routeWithArgs = "$route/{$ARG_ID}"
        fun createRoute(id: String) = "$route/$id"
    }
    object DetailPost: Route("detail_post") {
        const val ARG_ID = "id"

        val routeWithArgs = "$route/{$ARG_ID}"
        fun createRoute(id: String) = "$route/$id"
    }
}


sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        selectedIcon = androidx.compose.material.icons.Icons.Filled.Home,
        unselectedIcon = androidx.compose.material.icons.Icons.Outlined.Home
    )
    object CreatePost : BottomNavItem(
        route = "create_post", // Harus sama dengan Route.CreatePost.route
        title = "Create",
        selectedIcon = androidx.compose.material.icons.Icons.Outlined.Add, // Gantilah dengan ikon kustom ala Threads jika ada
        unselectedIcon = androidx.compose.material.icons.Icons.Outlined.Add
    )
    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        selectedIcon = androidx.compose.material.icons.Icons.Filled.Person,
        unselectedIcon = androidx.compose.material.icons.Icons.Outlined.Person
    )
}

sealed interface NavigateEvent {
    data object PopUp : NavigateEvent
}