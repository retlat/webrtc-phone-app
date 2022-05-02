// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.hilt.android.gradle.plugin)
    }
}

plugins {
    id("com.android.application").version("7.1.3").apply(false)
    id("com.android.library").version("7.1.3").apply(false)
    id("org.jetbrains.kotlin.android").version("1.6.10").apply(false)
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
