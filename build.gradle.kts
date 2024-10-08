/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import eu.darkcube.build.UploadArtifacts
import org.gradle.internal.component.local.model.PublishArtifactLocalArtifactMetadata

plugins {
    `java-base`
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.idea.ext)
    id("eu.darkcube.darkcube")
}

val cloudnetJarPlugins = configurations.register("plugins") { isTransitive = false }
val cloudnetJarInject = configurations.register("inject") { isTransitive = false }
val cloudnetJar = configurations.register("cloudnetJar") { isTransitive = false }
val cloudnetJarWrapper = configurations.register("cloudnetJarPlugins") { isTransitive = false }
val cloudnetJarInclude = configurations.register("cloudnetJarInclude") { isTransitive = false }
val standaloneJar = configurations.register("standaloneJar") { isTransitive = false }


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
        from(cloudnetJarInclude) {
            rename { "darkcubesystem-agent.jar" }
        }
        cloudnetJar.get().incoming.files.singleFile.also {
            from(zipTree(it))
        }
        include(cloudnetJarInject.get(), this, "inject")
        include(cloudnetJarPlugins.get(), this, "plugins")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveClassifier = "cloudnet"
    }
    val standaloneJar = register<Jar>("standaloneJar") {
        dependsOn(standaloneJar)
        standaloneJar.get().incoming.files.forEach {
            from(zipTree(it))
        }
        archiveClassifier = "standalone"
    }
    assemble.configure {
        dependsOn(cloudnetJar)
        dependsOn(standaloneJar)
    }
    register<UploadArtifacts>("uploadCloudNetJar") {
        dependsOn(cloudnetJar)
        files.from(cloudnetJar.map { it.outputs.files.singleFile })
    }
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
    cloudnetJarInclude(projects.darkcubesystemImplementationCloudnet) { targetConfiguration = "agent" }

    cloudnetJarPlugins(projects.darkcubesystemImplementationMinestom) { targetConfiguration = "cloudnetPlugin" }
    cloudnetJarInject(projects.darkcubesystemImplementationMinestom) { targetConfiguration = "cloudnetInject" }
    cloudnetJarPlugins(projects.darkcubesystemImplementationBukkit) { targetConfiguration = "cloudnetPlugin" }
    cloudnetJarInject(projects.darkcubesystemImplementationBukkit) { targetConfiguration = "cloudnetInject" }
    cloudnetJarPlugins(projects.darkcubesystemImplementationVelocity) { targetConfiguration = "cloudnetPlugin" }

    standaloneJar(projects.darkcubesystemImplementationStandalone) { targetConfiguration = "standalone" }
    standaloneJar(projects.darkcubesystemImplementationBukkit) { targetConfiguration = "standalone" }
}