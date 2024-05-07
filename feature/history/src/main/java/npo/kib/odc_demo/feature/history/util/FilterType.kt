package npo.kib.odc_demo.feature.history.util

import kotlinx.datetime.Instant
import npo.kib.odc_demo.core.model.WalletTransaction

internal sealed interface FilterType {
    data class Name(val name: String? = null) : FilterType
    data class Date(val date: Instant? = null) : FilterType
    data class Wid(val wid: String? = null) : FilterType
    data class Received(val value: Boolean? = null) : FilterType
    data class AtmOnly(val value: Boolean? = null) : FilterType
    data object NONE : FilterType

    companion object {

        fun List<WalletTransaction>.filterWithType(filter: FilterType): List<WalletTransaction> {
            return when (filter) {
                is Date -> filter.date?.let { date -> filter { transaction -> transaction.date == date } }
                is Name -> filter.name?.let { name -> filter { transaction -> transaction.otherName?.lowercase() == name.lowercase() } }
                is Received -> filter.value?.let { isReceived -> filter { transaction -> transaction.isReceived == isReceived } }
                is AtmOnly -> filter.value?.let { isAtmOnly -> filter { transaction -> transaction.isWithAtm == isAtmOnly } }
                is Wid -> filter.wid?.let { wid -> filter { transaction -> transaction.otherWid == wid } }
                NONE -> null
            } ?: this

        }

    }

}