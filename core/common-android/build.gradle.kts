plugins {
    alias(libs.plugins.odc.android.library)
    alias(libs.plugins.odc.android.library.compose)
    alias(libs.plugins.odc.android.hilt)
}

android {
    namespace = "npo.kib.odc_demo.common_android"
}

dependencies {
    implementation(libs.androidx.compose.runtime)
    implementation(libs.accompanist.permissions)
}