package plugins

import com.android.build.api.dsl.ApplicationExtension
import ext.getLibs
import ext.implementation
import ext.ksp
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class AppModulePlugin : Plugin<Project> {

    override fun apply(target: Project) {

        with(target) {
            with(pluginManager) {
                apply(getLibs().plugins.android.application.get().pluginId)
                apply(getLibs().plugins.compose.compiler.get().pluginId)
                apply(getLibs().plugins.android.hilt.get().pluginId)
                apply(getLibs().plugins.ksp.gradle.plugin.get().pluginId)
            }

            extensions.configure<ComposeCompilerGradlePluginExtension> {
                stabilityConfigurationFiles.addAll(
                    project.layout.projectDirectory.file("stability_config.conf")
                )
            }

            extensions.configure<ApplicationExtension> {

                compileSdk = getLibs().versions.compileSDk.get().toInt()

                buildFeatures {
                    resValues = true
                }

                defaultConfig {
                    applicationId = "by.niaprauski.nt"
                    versionCode = 1
                    versionName = "1.0"
                    minSdk = getLibs().versions.minSDK.get().toInt()
                    targetSdk = getLibs().versions.compileSDk.get().toInt()
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }

                    debug {
                        isMinifyEnabled = false
                        applicationIdSuffix = ".debug"
                        resValue("string", "app_name", "Next track debug")
                    }
                }

                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                        excludes += "/META-INF/gradle/incremental.annotation.processors"
                    }
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.toVersion(getLibs().versions.javaVersion.get())
                    targetCompatibility = JavaVersion.toVersion(getLibs().versions.javaVersion.get())
                }


                dependencies {
                    implementation(getLibs().androidx.core.ktx)
                    implementation(getLibs().androidx.appcompat)

                }

            }

            dependencies {
                implementation(project(":navigation"))
                implementation(project(":domain"))
                implementation(project(":data"))
                implementation(project(":uikit:design-system"))
                implementation(project(":translations"))

                implementation(getLibs().android.room.runtime)
                implementation(getLibs().android.room.ktx)
                ksp(getLibs().android.ksp.room.compiler)

                implementation(getLibs().androidx.datastore)

                implementation(getLibs().androidx.core.ktx)
                implementation(getLibs().androidx.appcompat)
                implementation(getLibs().androidx.viewmodel)
                implementation(getLibs().android.dagger.hilt)
                ksp(getLibs().ksp.hilt.compiler)
                implementation(getLibs().hilt.navigation)
                implementation(getLibs().kotlinx.coroutines.android)
                implementation(getLibs().androidx.lifecycle.runtime.ktx)
                implementation(getLibs().androidx.ui.graphics)
            }
        }
    }
}