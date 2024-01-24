buildscript{
   repositories{
       google()
       mavenCentral()
       maven { url = uri("https://kotlin.bintray.com/kotlinx") }
   }

    dependencies {
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }

}
plugins {
    id("com.android.application") version Build.androidBuildToolsVersion apply false
    id("org.jetbrains.kotlin.android") version Kotlin.version apply false
    //KSP instead of Kapt for faster builds
    id("com.google.devtools.ksp") version Build.KSPVersion apply false
//    kotlin("kapt") version "1.9.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version Kotlin.version apply false
    id("com.google.dagger.hilt.android") version DaggerHilt.version apply false
    id(Testing.junit5_plugin) version Testing.junit5_pluginVersion apply false
}