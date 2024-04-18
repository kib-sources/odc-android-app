plugins {
    id(Build.androidLibraryPlugin)
    id(Build.kotlinAndroidPlugin)
    id(Build.kspPlugin)
    id(Kotlin.kotlinSerializationPlugin)
}

android {
    namespace = "npo.kib.odc_demo.model"
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
    implementation(Room.roomRuntime)
    implementation(Room.roomKtx)
    implementation(project(":core:wallet"))
    implementation(project(":core:database"))
    implementation(project(":core:connectivity"))
//    implementation(project(":core:database"))
    ksp(Room.roomCompiler)

    //Serialization
    implementation(Serialization.serialization)
//    implementation(Serialization.googleGson)
}