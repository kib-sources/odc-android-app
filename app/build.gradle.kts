plugins {
    id("com.android.application")
    id("kotlin-android")
//    id("org.jetbrains.kotlin.kapt")
    //KSP instead of Kapt for faster builds
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.dagger.hilt.android")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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

dependencies {
    //AndroidX
    implementation(AndroidX.lifecycleViewmodel)
    implementation(AndroidX.lifecycleExtensions)
    implementation(AndroidX.lifecycleRuntime)
    implementation(AndroidX.preferenceKtx)
    implementation(AndroidX.legacySupport)
    implementation(AndroidX.coreKtx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.activity)

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
    //kapt(Room.roomCompiler)
    ksp(Room.roomCompiler)

    //Dagger-Hilt
    implementation(DaggerHilt.hiltAndroid)
    //KSP instead of Kapt for faster builds
    //kapt(DaggerHilt.hiltCompiler)
    ksp(DaggerHilt.hiltCompiler)
    implementation(DaggerHilt.hiltNavigationCompose)


//    Testing

//    testImplementation(Compose.composeBOM)
//    androidTestImplementation(Compose.composeBOM)
//
//    // Hilt testing dependency
//    androidTestImplementation (Testing.hiltTesting)
//    // Make Hilt generate code in the androidTest folder
//    kaptAndroidTest(DaggerHilt.hiltCompiler)
//
//
//    testImplementation(Testing.junit4)
//    testImplementation(Testing.junitAndroidExt)
//    testImplementation(Testing.truth)
//    testImplementation(Testing.coroutines)
//    testImplementation(Testing.turbine)
//    testImplementation(Testing.composeUiTest)
//    testImplementation(Testing.mockk)
//    testImplementation(Testing.mockWebServer)
//
//    androidTestImplementation(Testing.junit4)
//    androidTestImplementation(Testing.junitAndroidExt)
//    androidTestImplementation(Testing.truth)
//    androidTestImplementation(Testing.coroutines)
//    androidTestImplementation(Testing.turbine)
//    androidTestImplementation(Testing.composeUiTest)
//    androidTestImplementation(Testing.mockkAndroid)
//    androidTestImplementation(Testing.mockWebServer)
//
//
//    androidTestImplementation(Testing.testRunner)
}

//kapt {
//    correctErrorTypes = true
//    useBuildCache = true
//}