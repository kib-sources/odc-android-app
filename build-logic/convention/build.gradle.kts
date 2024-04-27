import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "npo.kib.odc_demo.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
    implementation(libs.truth)
    compileOnly(libs.junit5.plugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        val pluginsPackage = "npo.kib.odc_demo.plugins."
        register("androidApplication") {
            id = "odc.android.application"
            implementationClass = pluginsPackage + "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "odc.android.application.compose"
            implementationClass = pluginsPackage + "AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "odc.android.library"
            implementationClass = pluginsPackage + "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "odc.android.library.compose"
            implementationClass = pluginsPackage + "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "odc.android.feature"
            implementationClass = pluginsPackage + "AndroidFeatureConventionPlugin"
        }
        register("androidTest") {
            id = "odc.android.test"
            implementationClass = pluginsPackage + "AndroidTestConventionPlugin"
        }
        register("androidHilt") {
            id = "odc.android.hilt"
            implementationClass = pluginsPackage + "AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "odc.android.room"
            implementationClass = pluginsPackage + "AndroidRoomConventionPlugin"
        }
        register("androidFlavors") {
            id = "odc.android.application.flavors"
            implementationClass = pluginsPackage + "AndroidApplicationFlavorsConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "odc.kotlin.library"
            implementationClass = pluginsPackage + "KotlinLibraryConventionPlugin"
        }
    }
}