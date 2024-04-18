buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
    }

    dependencies {
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        classpath(Build.androidToolsBuildGradle)
    }

}
plugins {
    id(Build.kotlinAndroidPlugin) version Build.kotlinAndroidPluginVer apply false
    id(Build.androidApplicationPlugin) version Build.androidBuildToolsVersion apply false
    id(Build.androidLibraryPlugin) version Build.androidBuildToolsVersion apply false
    id(Build.kspPlugin) version Build.KSPVersion apply false
    id(DaggerHilt.hiltPlugin) version DaggerHilt.version apply false
    id(Kotlin.kotlinSerializationPlugin) version Kotlin.version apply false
    id(Testing.junit5_plugin) version Testing.junit5_pluginVersion apply false
}