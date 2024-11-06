

plugins {
    id("com.chaquo.python") version "16.0.0" apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false
    id ("com.google.dagger.hilt.android") version "2.48" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}