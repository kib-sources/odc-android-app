object Build {

    const val androidBuildToolsVersion = "8.3.2"
    const val kotlinAndroidPluginVer = Kotlin.version

    const val androidToolsBuildGradle = "com.android.tools.build:gradle:$androidBuildToolsVersion"
    const val androidToolsBuildGradleApi = "com.android.tools.build:gradle-api:$androidBuildToolsVersion"

    const val androidApplicationPlugin = "com.android.application"
    const val androidLibraryPlugin = "com.android.library"
    const val kotlinAndroidPlugin = "org.jetbrains.kotlin.android"

    const val KSPVersion = "1.9.22-1.0.18"
    const val kspPlugin = "com.google.devtools.ksp"
//    const val kspGradlePlugin = "com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin"
    const val kspGradlePlugin = "com.google.devtools.ksp.gradle.plugin"

    const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:2.0.4"
}