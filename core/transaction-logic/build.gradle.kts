plugins {
    alias(libs.plugins.odc.android.library)
    alias(libs.plugins.odc.android.hilt)
    alias(libs.plugins.odc.android.test)
}

android {
    namespace = "npo.kib.odc_demo.transaction_logic"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.commonJvm)
    implementation(projects.core.walletRepository)
    implementation(projects.core.wallet)
    implementation(projects.core.database)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.kotlinx.coroutines.test)
}