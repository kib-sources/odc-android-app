package npo.kib.odc_demo.plugins

import npo.kib.odc_demo.configs.getDefaultLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = getDefaultLibs()
            with(pluginManager) {
                apply("com.google.devtools.ksp")
                apply("dagger.hilt.android.plugin")
            }

            dependencies {
                "implementation"(libs.findLibrary("hilt.android").get())
                "ksp"(libs.findLibrary("hilt.compiler").get())
            }
        }
    }
}