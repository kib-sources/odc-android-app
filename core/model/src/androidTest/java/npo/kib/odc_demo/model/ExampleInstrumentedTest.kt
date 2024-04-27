<<<<<<<< HEAD:core/ui/src/androidTest/java/com/core/ui/ExampleInstrumentedTest.kt
package com.core.ui
========
package npo.kib.odc_demo.model
>>>>>>>> to_mm:core/model/src/androidTest/java/npo/kib/odc_demo/model/ExampleInstrumentedTest.kt

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
<<<<<<<< HEAD:core/ui/src/androidTest/java/com/core/ui/ExampleInstrumentedTest.kt
        assertEquals("com.core.ui.test", appContext.packageName)
========
        assertEquals("npo.kib.odc_demo.model.test", appContext.packageName)
>>>>>>>> to_mm:core/model/src/androidTest/java/npo/kib/odc_demo/model/ExampleInstrumentedTest.kt
    }
}