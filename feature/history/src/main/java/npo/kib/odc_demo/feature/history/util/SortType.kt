package npo.kib.odc_demo.feature.history.util

import kotlinx.coroutines.ensureActive
import npo.kib.odc_demo.core.model.WalletTransaction
import npo.kib.odc_demo.feature.history.util.SortOrder.*
import kotlin.coroutines.coroutineContext


internal sealed class SortType(open val sortOrder: SortOrder = DESCENDING) {

    data class ByName(override val sortOrder: SortOrder = DESCENDING) : SortType()
    data class ByType(override val sortOrder: SortOrder = DESCENDING) : SortType()
    data class ByDate(override val sortOrder: SortOrder = DESCENDING) : SortType()

    companion object {

        suspend fun List<WalletTransaction>.sortWithType(
            sortType: SortType
        ): List<WalletTransaction> {
            val c = coroutineContext
            return when (sortType.sortOrder) {
                DESCENDING -> when (sortType) {
                    is ByDate -> sortedByDescending { c.ensureActive(); it.date }
                    is ByName -> sortedByDescending { c.ensureActive(); it.otherName?.lowercase() }
                    is ByType -> sortedByDescending { c.ensureActive(); !it.isWithAtm }
                }
                ASCENDING -> when (sortType) {
                    is ByDate -> sortedBy { c.ensureActive(); it.date }
                    is ByName -> sortedBy { c.ensureActive(); it.otherName }
                    is ByType -> sortedBy { c.ensureActive(); !it.isWithAtm }
                }
            }
        }
    }

}


internal enum class SortOrder {
    ASCENDING,
    DESCENDING
}