/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import org.gradle.internal.component.local.model.PublishArtifactLocalArtifactMetadata

plugins {
    `java-base`
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.idea.ext)
    id("eu.darkcube.darkcube")
}

println(gradle.gradleUserHomeDir.canonicalPath)

val cloudnetJarPlugins = configurations.register("plugins") { isTransitive = false }
val cloudnetJarInject = configurations.register("inject") { isTransitive = false }
val cloudnetJar = configurations.register("cloudnetJar") { isTransitive = false }
val cloudnetJarWrapper = configurations.register("cloudnetJarPlugins") { isTransitive = false }

tasks {
    val cloudnetWrapperJar = register<Jar>("cloudnetWrapperJar") {
        dependsOn(cloudnetJarWrapper)
        cloudnetJarWrapper.get().incoming.files.singleFile.also {
            from(zipTree(it))
        }
        destinationDirectory = temporaryDir
    }
    val cloudnetJar = register<Jar>("cloudnetJar") {
        dependsOn(cloudnetWrapperJar)
        dependsOn(cloudnetJar)
        dependsOn(cloudnetJarInject)
        dependsOn(cloudnetJarPlugins)
        from(cloudnetWrapperJar) {
            rename { "darkcubesystem-wrapper.jar" }
        }
        cloudnetJar.get().incoming.files.singleFile.also {
            from(zipTree(it))
        }
        include(cloudnetJarInject.get(), this, "inject")
        include(cloudnetJarPlugins.get(), this, "plugins")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveClassifier = "cloudnet"
    }
    assemble.configure { dependsOn(cloudnetJar) }
}

fun include(configuration: Configuration, task: AbstractCopyTask, directory: String) {
    configuration.incoming.artifacts.forEach {
        val id = it.id
        val newName = if (id is PublishArtifactLocalArtifactMetadata) {
            val pa = id.publishArtifact
            "${pa.name}.${pa.extension}"
        } else {
            null
        }
        task.from(it.file) {
            into(directory)
            if (newName != null) {
                rename { newName }
            }
        }
    }
}

dependencies {
    cloudnetJar(projects.darkcubesystemImplementationCloudnet) { targetConfiguration = "node" }
    cloudnetJarWrapper(projects.darkcubesystemImplementationCloudnet) { targetConfiguration = "wrapper" }
    cloudnetJarPlugins(projects.darkcubesystemImplementationMinestom) { targetConfiguration = "cloudnetPlugin" }
    cloudnetJarInject(projects.darkcubesystemImplementationMinestom) { targetConfiguration = "cloudnetInject" }
    cloudnetJarPlugins(projects.darkcubesystemImplementationBukkit) { targetConfiguration = "cloudnetPlugin" }
    cloudnetJarInject(projects.darkcubesystemImplementationBukkit) { targetConfiguration = "cloudnetInject" }
    cloudnetJarPlugins(projects.darkcubesystemImplementationVelocity) { targetConfiguration = "cloudnetPlugin" }
}