plugins {
    alias(libs.plugins.odc.android.feature)
    alias(libs.plugins.odc.android.library.compose)
}

android {
    namespace = "npo.kib.odc_demo.feature.history"
}

dependencies {
//    implementation(projects.core.commonAndroid)
//    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(libs.kotlinx.datetime)
}