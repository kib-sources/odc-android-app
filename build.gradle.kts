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
    id("org.jetbrains.kotlin.kapt") version Kotlin.version apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.21" apply false
    id("com.google.dagger.hilt.android") version "2.47" apply false
//    id("com.android.library") version "7.4.2" apply false
}