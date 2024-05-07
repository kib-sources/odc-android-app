plugins {
    alias(libs.plugins.odc.kotlin.library)
}


dependencies {
    implementation(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.datetime)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.aggregator)
}