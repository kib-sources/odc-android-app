plugins {
    alias(libs.plugins.odc.android.feature)
    alias(libs.plugins.odc.android.library.compose)
}

android {
    namespace = "npo.kib.odc_demo.feature.atm"
}

dependencies {
    implementation(projects.core.datastore)
    implementation(projects.core.domain)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.commonJvm)
}