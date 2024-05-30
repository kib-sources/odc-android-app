plugins {
    alias(libs.plugins.odc.android.library)
    alias(libs.plugins.odc.android.room)
    alias(libs.plugins.odc.android.hilt)
}

android {
    namespace = "npo.kib.odc_demo.core.database"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.wallet)

    implementation(libs.kotlinx.datetime)

}