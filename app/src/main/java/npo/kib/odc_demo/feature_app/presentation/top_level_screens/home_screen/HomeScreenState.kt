package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen

import npo.kib.odc_demo.feature_app.domain.model.user.AppUser

data class HomeScreenState(
    val balance: Int = 0,
    val currentUser: AppUser = AppUser(),
    val isUpdatingBalanceAndInfo : Boolean = false
//    val historyState : HistoryState,
//    val transactionHistoryList: List<Int>
    //todo store past transactions and display in history block.
    // Can store failed transactions as well. Will have moved to multi-modular architecture
    // by the start of implementing history.
)