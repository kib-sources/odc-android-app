package npo.kib.odc_demo.core.common_jvm

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun getCurrentDateTimeAsInstant() : Instant = Clock.System.now()
