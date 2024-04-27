pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}



rootProject.name = "OpenDigitalCash"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
//feature
include(":feature:atm")
include(":feature:history")
include(":feature:home")
include(":feature:p2p")
include(":feature:settings")
include(":feature:wallet-details")
//core
include(":core:common-android")
include(":core:common-jvm")
include(":core:connectivity")
include(":core:database")
include(":core:datastore")
include(":core:design-system")
include(":core:domain")
include(":core:model")
include(":core:network")
include(":core:transaction-logic")
include(":core:ui")
include(":core:wallet")
include(":core:testing")
include(":core:wallet-repository")
