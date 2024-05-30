plugins {
    alias(libs.plugins.odc.android.library)
    alias(libs.plugins.odc.android.hilt)

}

android {
    namespace = "npo.kib.odc_demo.core.domain"
}

dependencies {
    implementation(projects.core.connectivity)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.commonJvm)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.model)
    implementation(projects.core.transactionLogic)
    implementation(projects.core.wallet)
    implementation(projects.core.walletRepository)
}