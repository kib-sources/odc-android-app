plugins {
    alias(libs.plugins.odc.android.feature)
    alias(libs.plugins.odc.android.library.compose)
}

android {
    namespace = "npo.kib.odc_demo.settings"
}

dependencies {

    implementation(projects.core.datastore)
    implementation(projects.core.commonAndroid)
}