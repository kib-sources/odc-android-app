plugins {
    alias(libs.plugins.odc.android.library)
    alias(libs.plugins.odc.android.hilt)
    alias(libs.plugins.odc.android.test)
 }

android {
    namespace = "npo.kib.odc_demo.core.testing"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

}

dependencies {

//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.test.espresso.core)
//    androidTestImplementation(libs.junit.jupiter.aggregator)
}