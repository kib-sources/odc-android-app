package npo.kib.odc_demo.plugins

import DaggerHilt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(Build.kspPlugin)
                apply(DaggerHilt.hiltPlugin)
            }
            dependencies {
                "implementation"(DaggerHilt.hiltAndroid)
                "ksp"(DaggerHilt.hiltCompiler)
            }
        }
    }
}