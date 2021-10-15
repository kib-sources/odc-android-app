package npo.kib.odc_demo.ui

import kotlinx.coroutines.flow.StateFlow

interface NearbyViewModel {
    val sum: StateFlow<Int?>
    fun getCurrentSum()
    fun acceptConnection()
    fun rejectConnection()
}