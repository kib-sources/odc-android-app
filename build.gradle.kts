buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://kotlin.bintray.com/kotlinx")
    }
}

plugins {
    val kotlin_version = "1.6.10"
    id("com.android.application") version "7.4.0" apply false
    id("org.jetbrains.kotlin.android") version "$kotlin_version" apply false
//        id 'com.google.dagger.hilt.android' version '2.45' apply false
//    id("org.jetbrains.kotlin.kapt") version "1.8.22" apply false
    id("org.jetbrains.kotlin.kapt") version "$kotlin_version" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10" apply false
//    id("com.android.library") version "7.4.0" apply false
}