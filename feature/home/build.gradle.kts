plugins {
    alias(libs.plugins.odc.android.feature)
    alias(libs.plugins.odc.android.library.compose)
}

android {
    namespace = "npo.kib.odc_demo.feature.home"
}

dependencies {
    implementation(projects.feature.p2p)
    implementation(projects.feature.atm)
    implementation(projects.feature.history)
    implementation(projects.feature.walletDetails)

    implementation(projects.core.datastore)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.wallet)

}