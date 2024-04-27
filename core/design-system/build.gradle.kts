plugins {
    alias(libs.plugins.odc.android.library)
    alias(libs.plugins.odc.android.library.compose)
}

android {
    namespace = "npo.kib.odc_demo.core.design_system"
}

dependencies {
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.ui.util)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.runtime)

}