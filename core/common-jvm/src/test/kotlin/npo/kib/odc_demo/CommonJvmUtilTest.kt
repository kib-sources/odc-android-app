package npo.kib.odc_demo

import npo.kib.odc_demo.core.common_jvm.containsPrefix
import npo.kib.odc_demo.core.common_jvm.isAValidAmount
import npo.kib.odc_demo.core.common_jvm.withoutPrefix
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CommonJvmUtilTest {

    @Test
    fun prefixConversionTest() {
        val prefix = "PREFIX"
        val a = prefix + "someValue"
        assertTrue { a.containsPrefix(prefix) }
        assertEquals(a, prefix + a.withoutPrefix(prefix))
    }

    @Test
    fun validIntAmountTest() {
        for(i in 1..1000) {
            val positiveInt = (1..1234).random().toString()
            val nonPositiveInt = (-1234..0).random().toString()
            assertTrue { positiveInt.isAValidAmount() }
            assertFalse { nonPositiveInt.isAValidAmount() }
        }
        val wrongAmount1 = "NotANumber"
        val wrongAmount2 = 1.1f.toString()
        assertFalse { wrongAmount1.isAValidAmount() || wrongAmount2.isAValidAmount()}
    }
}