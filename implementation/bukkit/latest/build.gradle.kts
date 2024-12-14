/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.paperweight.userdev)
    id("eu.darkcube.darkcube")
}

val cloudnetSource by sourceSets.register("cloudnet")

configurations.named(cloudnetSource.compileClasspathConfigurationName).configure {
    extendsFrom(configurations.compileClasspath.get())
}

dependencies {
    paperweightDevelopmentBundle(libs.paper.latest.bundle)
    implementation(projects.darkcubesystemImplementationBukkit)
    compileOnly(libs.cloudnet.driver)
    api(libs.viaversion.common)

    cloudnetSource.implementationConfigurationName(projects.darkcubesystemApiCloudnet)
    cloudnetSource.implementationConfigurationName(projects.darkcubesystemImplementationProvider)
    cloudnetSource.implementationConfigurationName(projects.darkcubesystemImplementationBukkit) { targetConfiguration = "cloudnetPluginRaw" }
    cloudnetSource.implementationConfigurationName(sourceSets.main.map { it.output })
    cloudnetSource.implementationConfigurationName(libs.cloudnet.wrapper)
}

val cloudnetJar = tasks.register<Jar>("cloudnetJar") {
    from(sourceSets.main.map { it.output })
    from(cloudnetSource.output)
    archiveClassifier = "cloudnet"
}
val standaloneJar = tasks.register<Jar>("standaloneJar") {
    from(sourceSets.main.map { it.output })
    archiveClassifier = "standalone"
}

tasks {
    jar { enabled = false }
}

configurations.consumable("version-cloudnet") {
    outgoing.artifact(cloudnetJar) {
        name = "latest"
    }
}
configurations.consumable("version-standalone") {
    outgoing.artifact(standaloneJar) {
        name = "latest"
    }
}
