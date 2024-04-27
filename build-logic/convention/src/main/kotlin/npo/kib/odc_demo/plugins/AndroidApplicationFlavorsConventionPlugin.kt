package npo.kib.odc_demo.plugins

import com.android.build.api.dsl.ApplicationExtension
import npo.kib.odc_demo.configs.configureFlavors
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationFlavorsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<ApplicationExtension> {
                configureFlavors(this)
            }
        }
    }
}