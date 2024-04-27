package npo.kib.odc_demo.plugins

import androidx.room.gradle.RoomExtension
import com.google.devtools.ksp.gradle.KspExtension
import npo.kib.odc_demo.configs.getDefaultLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val libs = getDefaultLibs()

            pluginManager.apply("androidx.room")
            pluginManager.apply("com.google.devtools.ksp")

            extensions.configure<KspExtension> {
                //with ksp, if set to true, causes build error when using abstract dao objects.
                arg("room.generateKotlin", "false")
            }

            extensions.configure<RoomExtension> {
                // The schemas directory contains a schema file for each version of the Room database.
                // This is required to enable Room auto migrations.
                // See https://developer.android.com/reference/kotlin/androidx/room/AutoMigration.
                schemaDirectory("$projectDir/schemas")
            }

            dependencies {
                add("runtimeOnly", libs.findLibrary("room.runtime").get())
                add("implementation", libs.findLibrary("room.ktx").get())
                add("ksp", libs.findLibrary("room.compiler").get())
            }
        }
    }
}