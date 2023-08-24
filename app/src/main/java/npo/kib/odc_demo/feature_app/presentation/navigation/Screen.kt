package npo.kib.odc_demo.feature_app.presentation.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen(route = "home_screen" )
    sealed class P2PScreen(p2pRoute: String) : Screen(route = "p2p_screen_$p2pRoute"){
        object SendNFC : P2PScreen("send_nfc")
        object RequestNFC : P2PScreen("request_nfc")
        object TopUpNFC : P2PScreen("top_up_nfc")
    }
    object SettingsScreen : Screen(route = "settings_sreen")

    object HistoryScreen : Screen(route = "history_screen")
}

val requestNFC = Screen.P2PScreen.RequestNFC.route
val sendNFC = Screen.P2PScreen.SendNFC.route

//check my kotlintest project in intellij idea