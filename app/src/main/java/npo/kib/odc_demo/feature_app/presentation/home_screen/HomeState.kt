package npo.kib.odc_demo.feature_app.presentation.home_screen

import npo.kib.odc_demo.feature_app.presentation.history_screen.HistoryState

data class HomeState(
    val balance: Int = 0,
    val historyState : HistoryState
)