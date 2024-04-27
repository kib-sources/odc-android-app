package npo.kib.odc_demo.transaction_logic.algorithms

import kotlinx.coroutines.ensureActive
import npo.kib.odc_demo.wallet.model.Amount
import kotlin.coroutines.coroutineContext


/** Generic solution with IntArray for numbers and Int for target sum.
 *  Subset Sum Problem, NP-Hard.
 *  Solved here with Dynamic Programming.
 *  @see <a href="https://en.wikipedia.org/wiki/Subset_sum_problem">Subset Sum Problem</a>
 * */
fun findSubsetWithSum(
    numbers: IntArray, targetSum: Int
): IntArray? {
    val n = numbers.size
    val dp = Array(n + 1) { IntArray(targetSum + 1) }

    for (i in 1..n) {
        for (j in 1..targetSum) {
            if (numbers[i - 1] > j) {
                dp[i][j] = dp[i - 1][j]
            } else {
                dp[i][j] = maxOf(dp[i - 1][j], dp[i - 1][j - numbers[i - 1]] + numbers[i - 1])
            }
        }
    }

    if (dp[n][targetSum] != targetSum) {
        return null
    }

    val subset = mutableListOf<Int>()
    var i = n
    var j = targetSum
    while (i > 0 && j > 0) {
        if (dp[i][j] != dp[i - 1][j]) {
            subset.add(numbers[i - 1])
            j -= numbers[i - 1]
        }
        i--
    }
    return subset.toIntArray()
}


/**
 *  Takes in banknotes [Amount]s and target sum.
 *
 *  Subset Sum Problem, NP-Hard.
 *  Solved here with Dynamic Programming.
 *  @see <a href="https://en.wikipedia.org/wiki/Subset_sum_problem">Subset Sum Problem</a>
 * */
fun _findBanknotesWithSum(
    banknotesIdsAmounts: List<npo.kib.odc_demo.wallet.model.Amount>, targetSum: Int
): List<npo.kib.odc_demo.wallet.model.Amount>? {
    if (targetSum <= 0 || banknotesIdsAmounts.isEmpty()) return null

    val n = banknotesIdsAmounts.size
    val amounts = IntArray(n) { banknotesIdsAmounts[it].amount }
    val dpTable = Array(n + 1) { IntArray(targetSum + 1) }

    for (i in 1..n) {
        for (j in 1..targetSum) {
            if (amounts[i - 1] > j) {
                dpTable[i][j] = dpTable[i - 1][j]
            } else {
                dpTable[i][j] =
                    maxOf(dpTable[i - 1][j], dpTable[i - 1][j - amounts[i - 1]] + amounts[i - 1])
            }
        }
    }

    if (dpTable[n][targetSum] != targetSum) return null

    val selectedBanknotes = mutableListOf<npo.kib.odc_demo.wallet.model.Amount>()
    var i = n
    var j = targetSum
    while (i > 0 && j > 0) {
        if (dpTable[i][j] != dpTable[i - 1][j]) {
            selectedBanknotes.add(banknotesIdsAmounts[i - 1])
            j -= amounts[i - 1]
        }
        i--
    }
    return selectedBanknotes.takeIf { it.isNotEmpty() }
}

/**
 *  More memory-efficient algorithm with [Boolean] stored in dp table.
 *  - 100%-tested.
 *  - Takes in banknotes [Amount]s and target sum.
 *  - Subset Sum Problem, NP-Hard, see link below.
 *  - Solved here with Dynamic Programming.
 *  @see <a href="https://en.wikipedia.org/wiki/Subset_sum_problem">Subset Sum Problem</a>
 * */
suspend fun findBanknotesWithSum(banknotesIdsAmounts: List<npo.kib.odc_demo.wallet.model.Amount>, targetSum: Int): List<npo.kib.odc_demo.wallet.model.Amount>? {
    if (targetSum <= 0 || banknotesIdsAmounts.isEmpty()) return null

    coroutineContext.ensureActive()
    val n = banknotesIdsAmounts.size
    val dp: Array<Array<Boolean>> = Array(n + 1) {
        coroutineContext.ensureActive()
        Array(targetSum + 1) {
            coroutineContext.ensureActive()
            it == 0
        }
    }

    for (i in 1..n) {
        for (j in 1..targetSum) {
            coroutineContext.ensureActive()
            dp[i][j] =
                dp[i - 1][j] || (j >= banknotesIdsAmounts[i - 1].amount && dp[i - 1][j - banknotesIdsAmounts[i - 1].amount])
        }
    }
    if (!dp[n][targetSum]) return null
    val selectedBanknotes = mutableListOf<npo.kib.odc_demo.wallet.model.Amount>()
    var i = n
    var j = targetSum
    while (i > 0 && j > 0) {
        coroutineContext.ensureActive()
        if (dp[i][j] && !dp[i - 1][j]) {
            selectedBanknotes.add(banknotesIdsAmounts[i - 1])
            j -= banknotesIdsAmounts[i - 1].amount
        }
        i--
    }
    return selectedBanknotes.takeIf { it.isNotEmpty() }
}