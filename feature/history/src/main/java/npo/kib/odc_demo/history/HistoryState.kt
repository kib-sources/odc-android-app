package npo.kib.odc_demo.history

data class HistoryState(
//    val transactionList: List<ODCTransaction>
    val transactionList: List<Int>
){
    val isEmpty: Boolean get() = transactionList.isEmpty()

}