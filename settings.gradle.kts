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
include (":app")
include (":core")
include (":feature")
include (":feature:log_in:presentation")
include (":feature:top_up:presentation")
include (":feature:request_money:presentation")
include (":feature:send_money:presentation")
include (":feature:settings:presentation")
include (":feature:history:presentation")
include (":core:p2p")
include (":core:remote_server:domain")
include (":core:util")
include (":core:ui")
include (":core:design_system")
include (":core:p2p:generic")
include (":core:p2p:implementations")
include (":core:p2p:implementations:bluetooth")
include (":core:p2p:implementations:nearby")
include (":core:p2p:implementations:nfc")
include (":feature:home_screen")
include (":feature:home_screen:presentation")
