package plugins

import com.android.build.api.dsl.LibraryExtension
import ext.getLibs
import ext.implementation
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class BaseAndroidModulePlugin : Plugin<Project> {

    override fun apply(target: Project) {

        with(target) {

            extensions.configure<LibraryExtension> {

                compileSdk = getLibs().versions.compileSDk.get().toInt()

                defaultConfig {
                    minSdk = getLibs().versions.minSDK.get().toInt()

                    consumerProguardFiles("consumer-rules.pro")
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
        }
    }
}
