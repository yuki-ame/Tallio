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
        // FIXED: Added parentheses, double quotes, and uri() helper
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Tallio"
include(":app")