plugins {
    alias(libs.plugins.android.library)
    id("plugin.android.module")
    alias(libs.plugins.ksp.gradle.plugin)
    alias(libs.plugins.android.hilt)
    id("com.google.protobuf") version "0.10.0"
}

android {
    namespace = "by.niaprauski.data"
}

dependencies {

    implementation(project(":domain"))

    implementation(libs.android.room.runtime)
    implementation(libs.android.room.ktx)
    ksp(libs.android.ksp.room.compiler)

    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.core)
    implementation(libs.protobuf.javalite)

    implementation(libs.paging3.runtime)
    implementation(libs.paging3.compose)
    implementation(libs.android.room.paging)


    implementation(libs.android.dagger.hilt)
    ksp(libs.ksp.hilt.compiler)

}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.32.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                task.plugins.clear()
                task.builtins {
                    create("java") {
                        option("lite")
                    }
                }
            }
        }
    }
}