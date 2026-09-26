import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.android.lib.gradle.plugin)
    implementation(libs.kotlin.lib.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(libs.ksp.lib.gradle.plugin)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}



tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.javaVersion.get()))
    }
}

gradlePlugin {

    plugins {

        register("plugin.android.module") {
            id = "plugin.android.module"
            implementationClass = "plugins.BaseAndroidModulePlugin"
        }

        register("plugin.compose.module") {
            id = "plugin.compose.module"
            implementationClass = "plugins.ComposeModulePlugin"
        }

        register("plugin.feature.module") {
            id = "plugin.feature.module"
            implementationClass = "plugins.FeatureModulePlugin"
        }

        register("plugin.app.module") {
            id = "plugin.app.module"
            implementationClass = "plugins.AppModulePlugin"
        }

        register("plugin.tests"){
            id = "plugin.tests"
            implementationClass = "plugins.TestsPlugin"
        }
    }
}