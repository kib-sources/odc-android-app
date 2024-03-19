plugins {
    id("com.android.application")
    id("kotlin-android")
    //KSP instead of Kapt for faster builds
    id("com.google.devtools.ksp")
//    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.dagger.hilt.android")
    id(Testing.junit5_plugin)
}


android {
    namespace = "npo.kib.odc_demo"
    compileSdk = 34

    defaultConfig {
        applicationId = "npo.kib.odc_demo"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        multiDexEnabled = true

//        javaCompileOptions {
//            annotationProcessorOptions {
//                arguments += ["room.schemaLocation": "$projectDir/schemas".toString()]
//            }
//        }
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += "-Xcontext-receivers"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Kotlin.kotlinCompilerExtensionVersion
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform() // Enables JUnit Platform (JUnit 5 + JUnit 4)
}


dependencies {
    //AndroidX
    implementation(AndroidX.lifecycleViewmodel)
    implementation(AndroidX.lifecycleExtensions)
    implementation(AndroidX.lifecycleRuntime)
    implementation(AndroidX.lifecycleRuntimeCompose)
    implementation(AndroidX.preferenceKtx)
    implementation(AndroidX.legacySupport)
    implementation(AndroidX.coreKtx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.activity)
    implementation(AndroidX.splashScreen)

    //Compose
    implementation(platform(Compose.composeBOM))

    implementation(Compose.ui)
    implementation(Compose.uiToolingPreview)
    implementation(Compose.activity)
    implementation(Compose.navigation)
    implementation(Compose.windowSizeClass)
    implementation(Compose.constraintLayoutCompose)

    //Material
    implementation(Compose.material3)
    implementation (Google.material)

    debugImplementation(Compose.uiTooling)

    //Coroutines
    implementation(Coroutines.coroutinesAndroid)

    //Serialization
    implementation(Serialization.serialization)
    implementation(Serialization.googleGson)

    //CBOR
    implementation(CBOR.cbor)

    //Retrofit
    implementation(Retrofit.retrofit)
    implementation(Retrofit.gsonConverter)
    implementation(Retrofit.okHttp)
    implementation(Retrofit.okHttpLoggingInterfceptor)

    //Nearby
    implementation(Google.playServicesNearby)

    //HTTP networking
    implementation(Kittinunf.fuel)
    implementation(Kittinunf.fuelAndroid)
    implementation(Kittinunf.fuelCoroutines)

    //Cryptography. Check vulnerability reports on Maven periodically.
    implementation(BouncyCastle.bcprov)
    implementation(BouncyCastle.bcpkix)

    //Room
    implementation(Room.roomRuntime)
    implementation(Room.roomKtx)
    //KSP instead of Kapt for faster builds
//    kapt(Room.roomCompiler)
    ksp(Room.roomCompiler)

    //Dagger-Hilt
    implementation(DaggerHilt.hiltAndroid)
    //KSP instead of Kapt for faster builds
    //kapt(DaggerHilt.hiltCompiler)
    ksp(DaggerHilt.hiltCompiler)
//    kapt(DaggerHilt.hiltCompiler)
    implementation(DaggerHilt.hiltNavigationCompose)

    //Google Accompanist
    implementation(GoogleAccompanist.permissions)


    //Datastore preferences
    implementation(AndroidX.datastorePreferences)

    //Kotlin reflection API
    implementation(Kotlin.kotlinReflect)

//    Testing

    testImplementation(Compose.composeBOM)
    androidTestImplementation(Compose.composeBOM)

    // Hilt testing dependency
    androidTestImplementation (Testing.hiltTesting)
    // Make Hilt generate code in the androidTest folder
    kspAndroidTest(DaggerHilt.hiltCompiler)
//    kaptAndroidTest(DaggerHilt.hiltCompiler)
    //junit5
    testImplementation(platform(Testing.junitBOM))
    // (Required) Writing and executing Unit Tests on the JUnit Platform
    testImplementation(Testing.junit5_jupiter_api)
    testRuntimeOnly(Testing.junit5_jupiter_engine)

    androidTestImplementation(Testing.junit5_jupiter_api)
    // (Optional) If you need "Parameterized Tests"
    testImplementation(Testing.junit5_jupiter_params)

//    androidTestImplementation("de.mannodermaus.junit5:android-test-core:1.4.0")
//    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:1.4.0")

//    testImplementation(Testing.junitAndroidExt)
//    androidTestImplementation(Testing.junitAndroidExt)


    testImplementation(Testing.truth)
    androidTestImplementation(Testing.truth)

    testImplementation(Testing.coroutines)
    androidTestImplementation(Testing.coroutines)

    testImplementation(Testing.turbine)
    androidTestImplementation(Testing.turbine)

    testImplementation(Testing.composeUiTest)
    androidTestImplementation(Testing.composeUiTest)

    testImplementation(Testing.mockk)
    androidTestImplementation(Testing.mockkAndroid)

    testImplementation(Testing.mockWebServer)
    androidTestImplementation(Testing.mockWebServer)

    androidTestImplementation(Testing.testRunner)

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.3")
}
//
//kapt {
//    correctErrorTypes = true
//    useBuildCache = true
//}