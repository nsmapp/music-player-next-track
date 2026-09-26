package plugins


import ext.getLibs
import ext.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class TestsPlugin : Plugin<Project>{

    override fun apply(target: Project) {

        with(target){

            dependencies{
                testImplementation(getLibs().junit)
                testImplementation(getLibs().mockito.core)
                testImplementation(getLibs().mockito.kotlin)
                testImplementation(getLibs().kotlinx.coroutines.test)
                testImplementation(getLibs().truth)
            }
        }
    }


}