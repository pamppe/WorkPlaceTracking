package com.emill.workplacetracking

enum class Screen {
    LOGIN,
    PROFILE,
    REGISTER,
    START,
}

sealed class NavigationItem(val route: String) {
    data object Login : NavigationItem(Screen.LOGIN.name)
    data object Profile : NavigationItem(Screen.PROFILE.name)
    data object Register : NavigationItem(Screen.REGISTER.name)
    data object Start : NavigationItem(Screen.START.name)
}