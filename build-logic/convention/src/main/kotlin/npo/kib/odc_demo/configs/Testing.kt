package npo.kib.odc_demo.configs

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

/**
 * Configure Testing-specific options, with junit5
 */
internal fun Project.configureAndroidTestingDepsJunit5() {
    val libs = getDefaultLibs()
    dependencies {
        val junitBom = libs.findLibrary("junit-bom").get()
        add("testImplementation", platform(junitBom))
        add("androidTestImplementation", platform(junitBom))
        add("testImplementation", libs.findLibrary("junit-jupiter-api").get())
        add("testRuntimeOnly", libs.findLibrary("junit-jupiter-engine").get())
        add("testImplementation", libs.findLibrary("junit-jupiter-params").get())
        // (Optional) If you also have JUnit 4-based tests
//        add("testImplementation", libs.findLibrary("junit").get())
//        add("testRuntimeOnly", libs.findLibrary("junit-jupiter-vintageEngine").get())
    }

}

//internal fun Project.configureAndroidTestingDeps(
//    commonExtension: CommonExtension<*, *, *, *, *, *>,
//) {
//    val libs = getDefaultLibs()
//    commonExtension.apply {
//        dependencies {
//            val junitBom = libs.findLibrary("junit-bom").get()
//            add("testImplementation", platform(junitBom))
//            add("androidTestImplementation", platform(junitBom))
//            add("testImplementation", libs.findLibrary("").get())
//            add("testRuntimeOnly", libs.findLibrary("").get())
//        }
//    }
//}

fun LibraryExtension.enableUseJunitPlatformAndroid() =
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }


fun Project.enableUseJunitPlatform() = tasks.withType<Test> { useJUnitPlatform() }