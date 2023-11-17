//Implement Compose BOM beforehand:
//
//testImplementation(Compose.composeBOM)
//androidTestImplementation(Compose.composeBOM)
object Testing {

    const val junit5_pluginVersion = "1.10.0.0"
    const val junit5_plugin = "de.mannodermaus.android-junit5"


    private const val junitBOMVersion = "5.10.1"
    const val junitBOM = "org.junit:junit-bom:$junitBOMVersion"

    // Aggregator dependency on JUnit api, engine, and params
    // testImplementation
//    const val junit_jupiter = "org.junit.jupiter:junit-jupiter"

    //testImplementation, androidTestImplementation
    const val junit5_jupiter_api = "org.junit.jupiter:junit-jupiter-api"
//
//    //testRuntimeOnly
    const val junit5_jupiter_engine = "org.junit.jupiter:junit-jupiter-engine"
//
//    //testImplementation
    const val junit5_jupiter_params = "org.junit.jupiter:junit-jupiter-params"

    private const val junitAndroidExtVersion = "1.2.0-alpha01"
    const val junitAndroidExt = "androidx.test.ext:junit:$junitAndroidExtVersion"

    //pulled from BOM
    const val composeUiTest = "androidx.compose.ui:ui-test-junit4"

    private const val testRunnerVersion = "1.5.2"
    const val testRunner = "androidx.test:runner:$testRunnerVersion"

    //Truth
    private const val truthVersion = "1.1.5"
    const val truth = "com.google.truth:truth:$truthVersion"

    //Coroutines test
    private const val coroutinesTestVersion = "1.7.2"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesTestVersion"

    //Turbine: "A small testing library for kotlinx.coroutines Flow"
    private const val turbineVersion = "1.0.0"
    const val turbine = "app.cash.turbine:turbine:$turbineVersion"

    //    Dagger-Hilt
    const val hiltTesting = "com.google.dagger:hilt-android-testing:${DaggerHilt.version}"


    //Mock objects
    private const val mockWebServerVersion = "4.11.0"
    const val mockWebServer = "com.squareup.okhttp3:mockwebserver:$mockWebServerVersion"

    private const val mockkVersion = "1.13.5"
    const val mockk = "io.mockk:mockk:$mockkVersion"
    const val mockkAndroid = "io.mockk:mockk-android:$mockkVersion"

}
