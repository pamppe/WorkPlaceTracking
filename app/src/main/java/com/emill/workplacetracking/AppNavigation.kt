package com.emill.workplacetracking

enum class Screen {
    LOGIN,
    PROFILE,
    REGISTER
}

sealed class NavigationItem(val route: String) {
    data object Login : NavigationItem(Screen.LOGIN.name)
    data object Profile : NavigationItem(Screen.PROFILE.name)
    data object Register : NavigationItem(Screen.REGISTER.name)
}
