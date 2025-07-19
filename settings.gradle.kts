pluginManagement {
    repositories {
        google() // Correct way to include Google's Maven repository
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") } // Explicitly add JitPack for plugin resolution
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google() // Correct way to include Google's Maven repository
        mavenCentral()
        maven {
            url = uri("https://jitpack.io") // JitPack for dependency resolution
        }
    }
}

rootProject.name = "Material"
include(":app")