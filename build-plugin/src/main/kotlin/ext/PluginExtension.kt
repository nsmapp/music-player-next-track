package ext

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.the

fun Project.getLibs(): LibrariesForLibs = the<LibrariesForLibs>()

internal fun DependencyHandlerScope.implementation(
    dependency: Provider<MinimalExternalModuleDependency>
) {
    add("implementation", dependency)
}


internal fun DependencyHandlerScope.implementation(
    dependency: ProjectDependency
) {
    add("implementation", dependency)
}

internal fun DependencyHandlerScope.implementation(
    dependency: ConfigurableFileCollection
) {
    add("implementation", dependency)
}

internal fun DependencyHandlerScope.ksp(dependency: Provider<MinimalExternalModuleDependency>,) {
    add("ksp", dependency)
}

internal fun DependencyHandlerScope.testImplementation(
    dependency: Provider<MinimalExternalModuleDependency>,
) {
    add("testImplementation", dependency)
}