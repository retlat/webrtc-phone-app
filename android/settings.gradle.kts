import java.net.URI

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = URI("https://jitpack.io") }
    }
    versionCatalogs {
        create("libs") {
            listOf(
                "hilt-android",
                "hilt-android-compiler",
                "hilt-android-gradle-plugin"
            ).forEach {
                library(it, "com.google.dagger:${it}:2.38.1")
            }

            library("androidx-core", "androidx.core:core-ktx:1.7.0")

            val composeVersion = "1.1.1"
            version("compose", composeVersion)
            val composeLibs = mapOf(
                Pair("compose-ui", "androidx.compose.ui:ui"),
                Pair("compose-ui-tooling", "androidx.compose.ui:ui-tooling"),
                Pair("compose-ui-tooling-preview", "androidx.compose.ui:ui-tooling-preview"),
                Pair("compose-material", "androidx.compose.material:material"),
            )
            composeLibs.forEach {
                library(it.key, "${it.value}:${composeVersion}")
            }
            library("lifecycle-runtime-ktx", "androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
            library("activity-compose", "androidx.activity:activity-compose:1.4.0")
            bundle(
                "compose",
                composeLibs.keys
                    .filter { it != "compose-ui-tooling" }
                    .plus(listOf("lifecycle-runtime-ktx", "activity-compose"))
            )

            library("webrtc", "com.github.webrtc-sdk:android:97.4692.01")
        }
        create("testingLibs") {
            library("junit", "junit:junit:4.13.2")
            library("junit-ext", "androidx.test.ext:junit:1.1.3")
            library("espresso", "androidx.test.espresso:espresso-core:3.4.0")
            library("compose", "androidx.compose.ui:ui-test-junit4:1.1.1")
        }
    }
}
rootProject.name = "PhoneApp"
include(
    ":app",
    ":resource",
    ":service:call",
    ":ui:main",
    ":ui:theme"
)
