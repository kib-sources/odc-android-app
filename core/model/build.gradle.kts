plugins {
    alias(libs.plugins.odc.kotlin.library)
}

dependencies {
    implementation(libs.kotlin.stdlib)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.aggregator)

}