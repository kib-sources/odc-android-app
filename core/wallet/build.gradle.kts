import npo.kib.odc_demo.configs.enableUseJunitPlatform
import npo.kib.odc_demo.configs.enableUseJunitPlatformAndroid

plugins {
    alias(libs.plugins.odc.android.library)
    alias(libs.plugins.odc.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "npo.kib.odc_demo.core.wallet"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

}

enableUseJunitPlatform()

dependencies {
    implementation(projects.core.commonAndroid)
    implementation(projects.core.commonJvm)
    implementation(projects.core.model)

    implementation(libs.bouncycastle.pkix)
    implementation(libs.kotlinx.serialization)
    implementation(libs.cbor)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.aggregator)
    testImplementation(libs.kotlinx.coroutines.test)
}

