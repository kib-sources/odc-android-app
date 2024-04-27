plugins {
    alias(libs.plugins.odc.android.library)
    alias(libs.plugins.odc.android.hilt)
}

android {
    namespace = "npo.kib.odc_demo.core.network"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.core.commonAndroid)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)
    implementation(projects.core.model)
    implementation(projects.core.wallet)
    //HTTP networking
    implementation(libs.kittinunf.fuel)
    implementation(libs.kittinunf.fuel.android)
    implementation(libs.kittinunf.fuel.coroutines)
}