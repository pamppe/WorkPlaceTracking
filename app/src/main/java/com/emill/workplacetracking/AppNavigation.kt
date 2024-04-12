package com.emill.workplacetracking

sealed class NavigationItem(val route: String) {
    object Login : NavigationItem("login")
    object Profile : NavigationItem("profile")
}
