package npo.kib.odc_demo.plugins

import AndroidX
import DaggerHilt
import Testing
import com.android.build.api.dsl.LibraryExtension
import npo.kib.odc_demo.configs.configureGradleManagedDevices
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("odc_demo.android.library")
                apply("odc_demo.android.hilt")
            }
            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
                @Suppress("UnstableApiUsage")
                testOptions.animationsDisabled = true
                configureGradleManagedDevices(this)
            }

            dependencies {
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:designsystem"))

                add("implementation", DaggerHilt.hiltNavigationCompose)
                add("implementation", AndroidX.lifecycleRuntimeCompose)
                add("implementation", AndroidX.lifecycleViewmodelCompose)
                add("implementation", AndroidX.tracingKtx)

                add("androidTestImplementation", Testing.androidLifecycleRuntimeTesting)
            }
        }
    }
}
