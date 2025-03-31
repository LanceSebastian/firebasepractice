// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Add the classpath for Google services here if needed
        classpath("com.google.gms:google-services:4.4.2") // Add the correct version of google-services plugin
        // Add any other required dependencies for the build process
    }
}
