import ext.getLibs

plugins {
    id("java-library")
    id("plugin.tests")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.ksp.gradle.plugin)
}
java {
    sourceCompatibility = JavaVersion.toVersion(getLibs().versions.javaVersion.get())
    targetCompatibility = JavaVersion.toVersion(getLibs().versions.javaVersion.get())
}

dependencies{
    implementation(libs.hilt.java.inject)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.immutable.collections.list)

    implementation(libs.paging3.common)
}
