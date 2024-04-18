import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
//    id("com.google.devtools.ksp") version ("1.9.20-1.0.14")
//    id("room-gradle-plugin")
//    id("com.google.devtools.ksp.gradle.plugin")
}

repositories {
    google()
    mavenCentral()
//    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

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
// https://mvnrepository.com/artifact/com.android.tools.build/gradle-api
//    compileOnly("com.android.tools.build:gradle-api:8.3.2")
//    compileOnly("com.android.tools.build:gradle:8.3.2")
    compileOnly("com.android.tools.build:gradle:8.3.2")
// https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-gradle-plugin
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    compileOnly("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.22-1.0.18")
    compileOnly("androidx.room:room-gradle-plugin:2.6.1")
    // https://mvnrepository.com/artifact/com.android.test/com.android.test.gradle.plugin
    compileOnly("com.android.test:com.android.test.gradle.plugin:8.3.2")
    implementation("com.google.truth:truth:1.4.2")
    compileOnly("com.android.tools:common:31.3.2")
    implementation("androidx.baselineprofile:androidx.baselineprofile.gradle.plugin:1.2.3")
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
        register("jvmLibrary") {
            id = "odc.jvm.library"
            implementationClass = pluginsPackage + "JvmLibraryConventionPlugin"
        }
    }
}