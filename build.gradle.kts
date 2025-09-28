// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://maven.google.com/")
            name = "Google"
        }
    }
}

plugins {
    id("com.android.application") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android") version "2.2.10" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.2.10" apply false
    id("org.jetbrains.kotlin.kapt") version "2.2.10" apply false
    id("com.google.dagger.hilt.android") version "2.57.1" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false
}

allprojects {
    buildDir = File("Builds/${rootProject.name}/${project.name}")

    repositories {
        mavenCentral()
        google()
    }
}
