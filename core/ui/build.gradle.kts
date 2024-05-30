plugins {
    alias(libs.plugins.odc.android.library)
    alias(libs.plugins.odc.android.library.compose)
}

android {
    namespace = "npo.kib.odc_demo.core.ui"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(projects.core.designSystem)
    api(projects.core.model)
}