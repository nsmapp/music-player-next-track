plugins {
    id("plugin.feature.module")
}

android {
    namespace = "by.niaprauski.settings"
}

dependencies {
    implementation(project(":player-service"))
    implementation(libs.media3.mediasession)

}