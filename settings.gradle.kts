pluginManagement {
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

include(":app")
include(":core:design-system")
include(":feature:home")
include(":feature:settings")
include(":feature:connectivity")
include(":feature:atm")
include(":core:datastore")
include(":core:database")
include(":core:network")
include(":core:connectivity")
include(":core:wallet")
include(":feature:history")
include(":feature:wallet-details")
include(":feature:p2p")
include(":core:model")
include(":core:domain")
include(":core:transaction-logic")
include(":core:common")
include(":core:ui")
