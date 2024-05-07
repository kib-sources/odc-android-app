plugins {
    alias(libs.plugins.odc.android.feature)
    alias(libs.plugins.odc.android.library.compose)
}

android {
    namespace = "npo.kib.odc_demo.feature.p2p"
}

dependencies {
    implementation(projects.feature.atm)

    implementation(libs.accompanist.permissions)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.commonJvm)
    implementation(projects.core.connectivity)
    implementation(projects.core.transactionLogic)
    implementation(projects.core.datastore)
    implementation(projects.core.domain)
    implementation(projects.core.wallet)
}