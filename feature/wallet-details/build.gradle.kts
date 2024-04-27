plugins {
    alias(libs.plugins.odc.android.feature)
    alias(libs.plugins.odc.android.library.compose)
}

android {
    namespace = "npo.kib.odc_demo.wallet_details"
}

dependencies {

    implementation(projects.core.domain)
    implementation(project(":core:wallet"))
}