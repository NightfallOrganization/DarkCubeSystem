/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow) apply false
    id("eu.darkcube.darkcube")
}

val cloudnetInjectContent = configurations.register("cloudnetInjectContent") { isTransitive = false }
val pluginSourceSet by sourceSets.register("plugin")
val cloudnetSourceSet by sourceSets.register("cloudnet")
val standaloneSourceSet by sourceSets.register("standalone")

val cloudnetJar = tasks.register<Jar>("cloudnetJar") {
    from(cloudnetSourceSet.output)
    from(pluginSourceSet.output)
    destinationDirectory = temporaryDir
}
val cloudnetInjectJar = tasks.register<ShadowJar>("cloudnetInjectJar") {
    dependsOn(cloudnetInjectContent)
    configurations = listOf(cloudnetInjectContent.get())
    destinationDirectory = temporaryDir
}
val standaloneJar = tasks.register<Jar>("standaloneJar") {
    from(standaloneSourceSet.output)
    from(pluginSourceSet.output)
    archiveClassifier = "standalone"
}
tasks.named<ProcessResources>(cloudnetSourceSet.processResourcesTaskName).configure {
    expand(mapOf("version" to version))
    inputs.property("version", version)
}

tasks.jar.configure {
    destinationDirectory = temporaryDir
}
tasks.assemble.configure {
    dependsOn(cloudnetJar)
    dependsOn(cloudnetInjectJar)
    dependsOn(standaloneJar)
}

configurations.named(pluginSourceSet.implementationConfigurationName).configure {
    extendsFrom(configurations.compileClasspath.get())
}
configurations.named(cloudnetSourceSet.implementationConfigurationName).configure {
    extendsFrom(configurations.getByName(pluginSourceSet.compileClasspathConfigurationName))
}
configurations.named(standaloneSourceSet.implementationConfigurationName).configure {
    extendsFrom(configurations.getByName(pluginSourceSet.compileClasspathConfigurationName))
}

// TODO make standalone work without extension (or at all)
configurations.consumable("standalone") {
    outgoing.artifact(standaloneJar) {
        name = "minestom"
        classifier = ""
    }
}
configurations.consumable("cloudnetPlugin") {
    outgoing.artifact(cloudnetJar) {
        name = "minestom"
        classifier = ""
    }
}
configurations.consumable("cloudnetInject") {
    outgoing.artifact(cloudnetInjectJar) {
        name = "minestom"
        classifier = ""
    }
}

dependencies {
    implementation(projects.darkcubesystemImplementationProvider)
    api(projects.darkcubesystemMinestom)
    api(projects.darkcubesystemImplementationKyoriWrapper)
    api(projects.darkcubesystemImplementationServer)

    pluginSourceSet.implementationConfigurationName(sourceSets.main.map { it.output })

    cloudnetSourceSet.implementationConfigurationName(pluginSourceSet.output)
    cloudnetSourceSet.implementationConfigurationName(projects.darkcubesystemImplementationCloudnet)
    cloudnetSourceSet.implementationConfigurationName(projects.darkcubesystemServerCloudnet)

    standaloneSourceSet.implementationConfigurationName(pluginSourceSet.output)

    cloudnetInjectContent(projects.darkcubesystemMinestom)
    cloudnetInjectContent(projects.darkcubesystemKyoriWrapper)
    cloudnetInjectContent(projects.darkcubesystemImplementationKyoriWrapper)
    cloudnetInjectContent(projects.darkcubesystemServer)
    cloudnetInjectContent(projects.darkcubesystemServerCloudnet)
    cloudnetInjectContent(projects.darkcubesystemImplementationServer)
    cloudnetInjectContent(sourceSets.main.map { it.output })
}