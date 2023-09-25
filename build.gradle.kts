/*buildscript {
    repositories {
        google()
        mavenCentral()
//        maven(url = "https://kotlin.bintray.com/kotlinx")
    }
    dependencies {
//        classpath(Build.androidBuildTools)
//        classpath(Build.hiltAndroidGradlePlugin)
//        classpath(Build.kotlinGradlePlugin)
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
//        classpath("com.android.tools.build:gradle:7.4.2")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}*/
buildscript{
   repositories{
       google()
       mavenCentral()
   }

    dependencies {
//        classpath (Build.androidBuildTools)
//        classpath (Build.kotlinGradlePlugin)
    }

}
plugins {
    id("com.android.application") version Build.androidBuildToolsVersion apply false
    id("org.jetbrains.kotlin.android") version Kotlin.version apply false
//    id("org.jetbrains.kotlin.kapt") version Kotlin.version apply false
    //KSP instead of Kapt for faster builds
    id("com.google.devtools.ksp") version Build.KSPVersion apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10" apply false
    id("com.google.dagger.hilt.android") version "2.47" apply false

}