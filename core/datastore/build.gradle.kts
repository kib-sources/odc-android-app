plugins {
    alias(libs.plugins.odc.android.library)
    alias(libs.plugins.odc.android.hilt)
}

android {
    namespace = "npo.kib.odc_demo.core.datastore"
}

dependencies {
    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.androidx.dataStore.core)
    implementation(libs.kotlinx.coroutines.core)

}