package npo.kib.odc_demo

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.*
import kotlinx.coroutines.withContext
import npo.kib.odc_demo.feature_app.data.db.Amount
import npo.kib.odc_demo.feature_app.domain.transaction_logic.util.findBanknotesWithSum
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SubsetSumTest {


    val testDispatcher = StandardTestDispatcher()
    val testScope = TestScope(testDispatcher + Job())

    @Test
    @DisplayName("Test if the current Subset Sum Problem algorithm works reliably")
    fun testAllFindBanknotesWithSum() {
        println(testPreselectedBanknotes(81, shouldSucceed = true))
        println(testPreselectedBanknotes(2000, shouldSucceed = false))

        testWithRandomlyGeneratedBanknotesListsAndTargetSums(runs = 100, maxTargetSum = 10000)
    }

    private fun testPreselectedBanknotes(sum: Int, shouldSucceed: Boolean) = testScope.runTest {
        val amounts = listOf(
            Amount("0", 14),
            Amount("1", 7),
            Amount("2", 3),
            Amount("3", 2),
            Amount("4", 10),
            Amount("5", 41),
            Amount("6", 2),
            Amount("7", 2),
            Amount("8", 2),
            Amount("9", 1000),
        )
        val res = findBanknotesWithSum(amounts, sum)
        if (shouldSucceed) assertNotNull(res) else assertNull(res)
    }

    private fun testWithRandomlyGeneratedBanknotesListsAndTargetSums(
        runs: Int, maxTargetSum: Int
    ) {
        testScope.runTest {
            var failed = 0
            for (i in 1..runs) {
                val randSum = (1..maxTargetSum).random()
                val randList = getRandomArrayFromSum(randSum)
                val resultSubset = findBanknotesWithSum(randList, randSum)!!
                val randListAmounts = randList.map { it.amount }
                val resultSubsetAmounts = resultSubset.map { it.amount }
                val isRunSuccessful = randListAmounts.toList()
                    .containsIncludingDuplicates(resultSubsetAmounts.toList())
                if (!isRunSuccessful) failed++
            }
            assertEquals(failed, 0)
        }
    }

    private fun getRandomArrayFromSum(sum: Int): List<Amount> {
        var remainingSum = sum
        val res = mutableListOf<Amount>()
        var index = 0
        val divider = 3
        while (remainingSum > 0) {
//            val randAm = (1..remainingSum).random()
            //allows for a larger amount of numbers in array
            val randAm1 =
                if (remainingSum / divider != 0) (1..remainingSum / divider).random() else (1..remainingSum).random()
            res.add(Amount(bnid = "${index++}", randAm1))
            remainingSum -= randAm1
            if (remainingSum < 0) throw Exception("REMAINING SUM < 0")
        }
        return res.toList()
    }


    private fun List<Int>.containsIncludingDuplicates(subList: List<Int>): Boolean {
        if (subList.size > this.size) {
            return false
        }

        val frequencyMap = mutableMapOf<Int, Int>()

        for (element in this) {
            frequencyMap[element] = frequencyMap.getOrDefault(element, 0) + 1
        }

        for (element in subList) {
            val frequency = frequencyMap.getOrDefault(element, 0)
            if (frequency == 0) {
                return false
            } else {
                frequencyMap[element] = frequency - 1
            }
        }

        return true
    }
}