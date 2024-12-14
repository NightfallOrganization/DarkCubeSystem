/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import org.gradle.internal.component.local.model.PublishArtifactLocalArtifactMetadata

plugins {
    `java-library`
    `maven-publish`
    id("eu.darkcube.darkcube")
}

val cloudnetSource by sourceSets.register("cloudnet")
val standaloneSource by sourceSets.register("standalone")
val merge = configurations.register("merge") { isTransitive = false }
val inject = configurations.register("inject") { isTransitive = false }
val mergeCloudNet = configurations.register("mergeCloudNet") {
    isTransitive = false
    extendsFrom(merge.get())
}
val injectCloudNet = configurations.register("injectCloudNet") {
    isTransitive = false
    extendsFrom(inject.get())
}
val mergeStandalone = configurations.register("mergeStandalone") {
    isTransitive = false
    extendsFrom(merge.get())
}
val injectStandalone = configurations.register("injectStandalone") {
    isTransitive = false
    extendsFrom(inject.get())
}

configurations.named(cloudnetSource.implementationConfigurationName).configure {
    extendsFrom(configurations.getByName(sourceSets.main.get().compileClasspathConfigurationName))
}
configurations.named(standaloneSource.implementationConfigurationName).configure {
    extendsFrom(configurations.getByName(sourceSets.main.get().compileClasspathConfigurationName))
}
tasks.named<ProcessResources>(cloudnetSource.processResourcesTaskName) {
    expand(mapOf("version" to version))
    inputs.property("version", version)
}
tasks.named<ProcessResources>(standaloneSource.processResourcesTaskName) {
    expand(mapOf("version" to version))
    inputs.property("version", version)
}
tasks.jar.configure {
    destinationDirectory = temporaryDir
}
val cloudnetJar = tasks.register<Jar>("cloudnetJar") {
    dependsOn(mergeCloudNet)
    includeVersions(mergeCloudNet.get(), this)
    from(cloudnetSource.output)
    from(sourceSets.main.map { it.output })
    destinationDirectory = temporaryDir
}
val cloudnetJarRaw = tasks.register<Jar>("cloudnetJarRaw") {
    from(cloudnetSource.output)
    destinationDirectory = temporaryDir
}
val cloudnetInjectJar = tasks.register<Jar>("cloudnetInjectJar") {
    dependsOn(injectCloudNet)
    injectCloudNet.get().forEach { from(zipTree(it)) }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    destinationDirectory = temporaryDir
}
val standaloneJar = tasks.register<Jar>("standaloneJar") {
    dependsOn(mergeStandalone)
    dependsOn(injectStandalone)
    includeVersions(mergeStandalone.get(), this)
    injectStandalone.get().forEach { from(zipTree(it)) }
    from(standaloneSource.output)
    from(sourceSets.main.map { it.output })
    destinationDirectory = temporaryDir
}
tasks.assemble.configure {
    dependsOn(cloudnetJar)
    dependsOn(cloudnetInjectJar)
}

configurations.consumable("cloudnetPlugin") {
    outgoing.artifact(cloudnetJar) {
        name = "bukkit"
    }
}
configurations.consumable("cloudnetInject") {
    outgoing.artifact(cloudnetInjectJar) {
        name = "bukkit"
    }
}
configurations.consumable("cloudnetPluginRaw") {
    outgoing.artifact(cloudnetJarRaw)
}
configurations.consumable("standalone") {
    outgoing.artifact(standaloneJar)
}
val versions = ArrayList<String>()
// KEEP THESE VERSIONS IN CORRECT ORDER!!!
registerVersion("v1_8_R3")
registerVersion("latest")
val generateVersionsMeta = tasks.register("generateVersionsMeta") {
    inputs.property("versions", versions.toString())
    val file = temporaryDir.resolve("versions")
    val v = versions
    doLast {
        file.writeText(v.joinToString("\n"))
    }
    outputs.file(file)
}

dependencies {
    compileOnly(libs.paper.latest)
    compileOnly(libs.viaversion)
    api(projects.darkcubesystemBukkit)
    api(projects.darkcubesystemImplementationKyoriWrapper)
    api(projects.darkcubesystemImplementationServer)
    "standaloneImplementation"(projects.darkcubesystemImplementationStandalone)

    cloudnetSource.implementationConfigurationName(sourceSets.main.map { it.output })
    cloudnetSource.implementationConfigurationName(libs.luckperms)
    cloudnetSource.implementationConfigurationName(projects.darkcubesystemServerCloudnet)
    cloudnetSource.implementationConfigurationName(projects.darkcubesystemImplementationCloudnet)
    standaloneSource.implementationConfigurationName(sourceSets.main.map { it.output })

    merge(projects.darkcubesystemImplementationBukkit188) { targetConfiguration = "version" }
    mergeCloudNet(projects.darkcubesystemImplementationBukkitLatest) { targetConfiguration = "version-cloudnet" }
    mergeStandalone(projects.darkcubesystemImplementationBukkitLatest) { targetConfiguration = "version-standalone" }

    inject(projects.darkcubesystemBukkit)
    inject(projects.darkcubesystemServer)
    inject(projects.darkcubesystemKyoriWrapper)
    inject(projects.darkcubesystemImplementationKyoriWrapper)
    inject(projects.darkcubesystemImplementationServer)

    injectCloudNet(projects.darkcubesystemServerCloudnet)
}

fun includeVersions(configuration: Configuration, task: AbstractCopyTask) {
    include(configuration, task, "versions")
    task.from(generateVersionsMeta) { into("versions") }
}

fun registerVersion(name: String) {
    versions.add(name)
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