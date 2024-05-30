import npo.kib.odc_demo.configs.BuildVariantSigningKey
import npo.kib.odc_demo.configs.OdcBuildType

plugins {
    alias(libs.plugins.odc.android.application)
    alias(libs.plugins.odc.android.application.compose)
    alias(libs.plugins.odc.android.application.flavors)
    alias(libs.plugins.odc.android.hilt)
//    alias(libs.plugins.baselineprofile) can add later along with benchmarks module
//    alias(libs.plugins.module.graph)
}


android {
    namespace = "npo.kib.odc_demo"
    defaultConfig {
        applicationId = "npo.kib.odc_demo"

        versionCode = 3
        versionName = "2.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        /*Android application (APK) files contain executable bytecode files
         * in the form of Dalvik Executable (DEX) files, which contain the compiled
         * code used to run your app. The Dalvik Executable specification limits
         * the total number of methods that can be referenced within a single DEX file to 65,536
         * , including Android framework methods, library methods, and methods in your own code.
         * Getting past this limit requires that you configure your app build process to generate
         *  more than one DEX file, known as a multidex configuration.
         * P.S. : required to be enabled for core library desugaring. */
        multiDexEnabled = true //

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = OdcBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            applicationIdSuffix = OdcBuildType.RELEASE.applicationIdSuffix

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.named(BuildVariantSigningKey.DEBUG.key).get()
            // Ensure Baseline Profile is fresh for release builds.
//            baselineProfile.automaticGenerationDuringBuild = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform() // Enables JUnit Platform (JUnit 5 + JUnit 4)
    minHeapSize = "512m"
    maxHeapSize = "2048m"
    jvmArgs = listOf("-XX:MaxPermSize=2048m")
}

dependencies {
    //Feature modules (top-level only)

    implementation(projects.feature.home)
    implementation(projects.feature.settings)

    //Core modules

    implementation(projects.core.commonAndroid)
//    implementation(projects.core.designSystem)
    implementation(projects.core.ui)
    implementation(projects.core.datastore)
    implementation(projects.core.domain)

//    AndroidX dependencies

//    implementation(AndroidX.lifecycleExtensions)
//    implementation(AndroidX.lifecycleRuntime)
    runtimeOnly(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
//    implementation(AndroidX.preferenceKtx)
//    implementation(AndroidX.legacySupport)
//    implementation(AndroidX.appCompat)
    implementation(libs.androidx.activity.activity)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
//
//    //Compose
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.runtime.android)

    ksp(libs.hilt.compiler)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    kspTest(libs.hilt.compiler)



}

//baselineProfile {
//    // Don't build on every iteration of a full assemble.
//    // Instead enable generation directly for the release build variant.
//    automaticGenerationDuringBuild = false
//}

//android.sourceSets.all {
//    kotlin.srcDir("src/$name/kotlin")
//}

//for the module this is declared in
//moduleGraphConfig {
//    readmePath = "${rootDir}/README.md"
//    heading = "# App Module Graph "
//    theme.set(Theme.FOREST)
//    linkText = LinkText.CONFIGURATION
//    setStyleByModuleType = true
//}

//If in root build.gradle then add (but still fails "need at least 2 modules in the project")
//tasks.findByName("createModuleGraph")?.apply {
//    doNotTrackState("Otherwise gradle may complain about a file lock of README.md")
//}