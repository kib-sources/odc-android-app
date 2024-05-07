plugins {
    alias(libs.plugins.odc.kotlin.library)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.datetime)
    implementation(libs.google.gson)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter.aggregator)


}