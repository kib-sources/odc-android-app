package npo.kib.odc_demo.feature_app.presentation.navigation


//Can use enum class containing destinations to iterate over it easily. Iterating over sealed classes is possible only through reflection
//See "Now In Android" official Google's app for best practices (TopLevelDestination class)
sealed class ScreenRoutes(val route: String) {
    object HomeScreen : ScreenRoutes(route = "home_screen")
    sealed class P2PScreen(p2pRoute: String) : ScreenRoutes(route = "p2p_screen_$p2pRoute"){
        object SendNFC : P2PScreen("send")
        object RequestNFC : P2PScreen("request")
        //Top up with NFC
        object ATM : P2PScreen("top_up_atm")
    }
    object SettingsScreen : ScreenRoutes(route = "settings_sreen")

    object HistoryScreen : ScreenRoutes(route = "history_screen")
}
