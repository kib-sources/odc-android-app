plugins {
    alias(libs.plugins.odc.android.library)
    alias(libs.plugins.odc.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "npo.kib.odc_demo.core.connectivity"
}

dependencies {

    implementation(projects.core.model)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.commonJvm)
    implementation(projects.core.wallet)

    implementation(libs.androidx.activity)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.activity.activity)
    implementation(libs.kotlinx.serialization)
    implementation(libs.cbor)

}