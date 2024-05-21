/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.idea.ext)
    id("eu.darkcube.darkcube")
}

val cloudnetSource by sourceSets.register("cloudnet")
val standaloneSource by sourceSets.register("standalone")

configurations.named(cloudnetSource.implementationConfigurationName).configure {
    extendsFrom(configurations.getByName(sourceSets.main.get().compileClasspathConfigurationName))
}
configurations.named(standaloneSource.implementationConfigurationName).configure {
    extendsFrom(configurations.getByName(sourceSets.main.get().compileClasspathConfigurationName))
}

val generateTemplates = tasks.register<Copy>("generateTemplates") {
    val properties = mapOf("version" to version)
    inputs.properties(properties)
    from(file("src/main/templates"))
    into(layout.buildDirectory.dir("generated/sources/templates"))
    expand(properties)
}
sourceSets.main.configure {
    java.srcDir(generateTemplates.map { it.outputs })
}
val cloudnetJar = tasks.register<Jar>("cloudnetJar") {
    from(cloudnetSource.output)
    destinationDirectory = temporaryDir
}
configurations.consumable("cloudnetPlugin") {
    outgoing.artifact(cloudnetJar) { name = "velocity" }
}

rootProject.idea.project.settings.taskTriggers.afterSync(generateTemplates)

dependencies {
    api(projects.darkcubesystemProxy)
    api(projects.darkcubesystemImplementationCommon)
    api(libs.velocity)

    standaloneSource.implementationConfigurationName(sourceSets.main.map { it.output })
    standaloneSource.annotationProcessorConfigurationName(libs.velocity)
    cloudnetSource.implementationConfigurationName(sourceSets.main.map { it.output })
    cloudnetSource.annotationProcessorConfigurationName(libs.velocity)

    cloudnetSource.implementationConfigurationName(projects.darkcubesystemImplementationCloudnet)
    cloudnetSource.implementationConfigurationName(libs.viaversion)
    cloudnetSource.implementationConfigurationName(libs.viaversion.common)
    cloudnetSource.implementationConfigurationName(libs.viaversion.velocity)
}