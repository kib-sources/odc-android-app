package npo.kib.odc_demo.feature_app.presentation.common.ui

data class HistoryState(
//    val transactionList: List<ODCTransaction>
    val transactionList: List<Int>
){
    val isEmpty: Boolean get() = transactionList.isEmpty()

}