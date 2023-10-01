package npo.kib.odc_demo.feature_app.presentation.top_level_screens.history_screen

data class HistoryState(
//    val transactionList: List<ODCTransaction>
    val transactionList: List<Int>
){
    val isEmpty: Boolean get() = transactionList.isEmpty()

}