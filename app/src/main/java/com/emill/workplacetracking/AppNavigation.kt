sealed class NavigationItem(val route: String) {
    object Login : NavigationItem("login")
    object Profile : NavigationItem("profile")
    object Gps : NavigationItem("gps")
    object Register : NavigationItem("register")
    object Start : NavigationItem("start")
    object Timer : NavigationItem("timer")

    object RequestAccess : NavigationItem("requestAccess")
}
