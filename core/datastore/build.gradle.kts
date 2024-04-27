plugins {
    alias(libs.plugins.odc.android.library)
    alias(libs.plugins.odc.android.hilt)
}

android {
    namespace = "npo.kib.odc_demo.datastore"
}

dependencies {
    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.androidx.dataStore.core)
    implementation(libs.kotlinx.coroutines.core)

}