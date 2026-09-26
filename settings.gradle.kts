pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Next track"

includeBuild("build-plugin")

include(":app")
include(":navigation")
include(":features:player")
include(":features:settings")
include(":features:library")
include(":utils")
include(":data")
include(":domain")
include(":player-service")
include(":uikit:design-system")
include(":translations")
include(":features:about")
