package plugins

import ext.getLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

class ComposeModulePlugin: Plugin<Project> {


    override fun apply(target: Project) {

        with(target){
            with(pluginManager){
                apply(getLibs().plugins.compose.compiler.get().pluginId)
                apply("plugin.android.module")
            }

            extensions.configure(ComposeCompilerGradlePluginExtension::class.java) {
                stabilityConfigurationFile.set(rootProject.layout.projectDirectory.file("compose_stability.conf"))
            }
        }
    }
}