package npo.kib.odc_demo.feature_app.presentation.util

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object SettingsScreen : Screen("settings_screen")
    object HistoryScreen : Screen("history_screen")
    object SendScreen : Screen("send_screen")
    object RequestScreen : Screen("request_screen")
    object TopUpScreen : Screen("top_up_screen")
}