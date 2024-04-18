plugins {
//    id(Build.androidApplicationPlugin)
    id(Build.kotlinAndroidPlugin)
    id(Build.androidLibraryPlugin)
    id(Build.kspPlugin)
    id(DaggerHilt.hiltPlugin)
}

android {
    namespace = "npo.kib.odc_demo.domain"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    //Dagger-Hilt
    implementation(DaggerHilt.hiltAndroid)
    ksp(DaggerHilt.hiltCompiler)
}