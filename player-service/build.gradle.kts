plugins {
    alias(libs.plugins.ksp.gradle.plugin)
    alias(libs.plugins.android.hilt)
    alias(libs.plugins.android.library)
    id("plugin.android.module")
}

android {
    namespace = "by.niaprauski.playerservice"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(project(":utils"))
    implementation(project(":domain"))
    implementation(project(":translations"))


    implementation(libs.media3.exoplayer)
    implementation(libs.media3.dash)
    implementation(libs.media3.ui)
    implementation(libs.media3.mediasession)

    implementation(libs.material)
    implementation(libs.hilt.java.inject)

    implementation(libs.android.dagger.hilt)
    ksp(libs.ksp.hilt.compiler)
}