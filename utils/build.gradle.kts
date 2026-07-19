plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    id("plugin.android.module")
}

android {
    namespace = "by.niaprauski.utils"
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.media3.mediasession)
    implementation(libs.kotlinx.coroutines.core)
}