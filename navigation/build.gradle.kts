plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    id("plugin.compose.module")
}

android {
    namespace = "by.niaprauski.navigation"
}

dependencies {

    implementation(project(":features:player"))
    implementation(project(":features:library"))
    implementation(project(":features:settings"))
    implementation(project(":features:about"))
    implementation(project(":uikit:design-system"))
    implementation(project(":translations"))

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.compose.material3)
}